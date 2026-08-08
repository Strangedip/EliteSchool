package com.eliteschool.wallet_service.service;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.common_utils.exception.AppException;
import com.eliteschool.wallet_service.client.AuthServiceClient;
import com.eliteschool.wallet_service.client.StoreServiceClient;
import com.eliteschool.wallet_service.client.TaskServiceClient;
import com.eliteschool.wallet_service.dto.TransactionDto;
import com.eliteschool.wallet_service.dto.TransactionMapper;
import com.eliteschool.wallet_service.dto.WalletDto;
import com.eliteschool.wallet_service.dto.WalletMapper;
import com.eliteschool.wallet_service.exception.InsufficientPointsException;
import com.eliteschool.wallet_service.exception.ItemOutOfStockException;
import com.eliteschool.wallet_service.exception.TasksNotCompletedException;
import com.eliteschool.wallet_service.model.Transaction;
import com.eliteschool.wallet_service.model.Wallet;
import com.eliteschool.wallet_service.model.enums.TransactionType;
import com.eliteschool.wallet_service.repository.TransactionRepository;
import com.eliteschool.wallet_service.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final StoreServiceClient storeServiceClient;
    private final TaskServiceClient taskServiceClient;
    private final AuthServiceClient authServiceClient;

    public int getWalletBalance(UUID studentId) {
        return walletRepository.findByStudentId(studentId)
                .map(Wallet::getBalance)
                .orElse(0);
    }

    @Transactional
    public void creditPoints(UUID studentId, int points, String description) {
        creditPoints(studentId, points, description, null);
    }

    @Transactional
    public void creditPoints(UUID studentId, int points, String description, String referenceId) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points must be positive");
        }
        if (referenceId != null && !referenceId.isBlank()
                && transactionRepository.existsByReferenceId(referenceId)) {
            log.info("Skipping duplicate award for reference {}", referenceId);
            return;
        }

        Wallet wallet = walletRepository.findByStudentId(studentId)
                .orElse(new Wallet(studentId, 0));

        wallet.setBalance(wallet.getBalance() + points);
        walletRepository.save(wallet);

        transactionRepository.save(Transaction.builder()
                .studentId(studentId)
                .transactionType(TransactionType.CREDIT)
                .points(points)
                .description(description)
                .referenceId(referenceId)
                .build());
    }

    @Transactional
    public boolean debitPoints(UUID studentId, int points, String description) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points must be positive");
        }
        Wallet wallet = walletRepository.findByStudentId(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found for student: " + studentId));

        if (wallet.getBalance() < points) {
            throw new InsufficientPointsException("Insufficient points: required " + points + ", available " + wallet.getBalance());
        }

        wallet.setBalance(wallet.getBalance() - points);
        walletRepository.save(wallet);

        transactionRepository.save(Transaction.builder()
                .studentId(studentId)
                .transactionType(TransactionType.DEBIT)
                .points(points)
                .description(description)
                .build());

        return true;
    }

    @Transactional
    public boolean purchaseItem(UUID studentId, UUID itemId) {
        log.info("Processing purchase: student={}, item={}", studentId, itemId);

        ResponseEntity<CommonResponseDto<StoreServiceClient.StoreItemView>> itemResponse =
                storeServiceClient.getItem(itemId);
        if (!itemResponse.getStatusCode().is2xxSuccessful()
                || itemResponse.getBody() == null
                || !itemResponse.getBody().isSuccess()
                || itemResponse.getBody().getData() == null) {
            throw new EntityNotFoundException("Item not found: " + itemId);
        }

        StoreServiceClient.StoreItemView item = itemResponse.getBody().getData();
        String acquisition = item.getAcquisitionType() != null
                ? item.getAcquisitionType().trim().toUpperCase(Locale.ROOT)
                : "POINTS";
        int price = item.getPrice() != null ? item.getPrice() : 0;
        List<UUID> requiredTasks = item.getRequiredTaskIds() != null
                ? item.getRequiredTaskIds()
                : List.of();

        ensureClaimWindowOpen(item);

        if (item.getStock() != null && item.getStock() <= 0) {
            throw new ItemOutOfStockException("Item out of stock or unavailable");
        }

        if ("TASKS".equals(acquisition) || "POINTS_AND_TASKS".equals(acquisition)) {
            ensureTasksCompleted(studentId, requiredTasks);
        }

        if ("POINTS".equals(acquisition) || "POINTS_AND_TASKS".equals(acquisition)) {
            if (price < 1) {
                throw new AppException(
                        "Invalid store item price",
                        "This item requires Elite Points but has an invalid price",
                        "INVALID_ITEM",
                        HttpStatus.BAD_REQUEST);
            }
            String debitDescription = "POINTS_AND_TASKS".equals(acquisition)
                    ? "Store (tasks + points): " + item.getName()
                    : "Purchase: " + item.getName();
            debitPoints(studentId, price, debitDescription);
        } else if ("TASKS".equals(acquisition) && price > 0) {
            log.warn("TASKS item {} has price {} — points not charged", itemId, price);
        } else if (!"TASKS".equals(acquisition) && !"POINTS".equals(acquisition)
                && !"POINTS_AND_TASKS".equals(acquisition)) {
            throw new AppException(
                    "Unknown acquisition type: " + acquisition,
                    "This store item has an unsupported acquisition type",
                    "INVALID_ITEM",
                    HttpStatus.BAD_REQUEST);
        }

        String studentName = resolveStudentName(studentId);
        ResponseEntity<CommonResponseDto<Object>> storeResponse =
                storeServiceClient.purchaseItem(itemId, studentId, studentName);
        if (!storeResponse.getStatusCode().is2xxSuccessful()
                || storeResponse.getBody() == null
                || !storeResponse.getBody().isSuccess()) {
            throw new ItemOutOfStockException("Item out of stock or unavailable");
        }

        if ("TASKS".equals(acquisition) && price <= 0) {
            transactionRepository.save(Transaction.builder()
                    .studentId(studentId)
                    .transactionType(TransactionType.DEBIT)
                    .points(0)
                    .description("Claimed via tasks: " + item.getName())
                    .build());
        }

        return true;
    }

    private void ensureClaimWindowOpen(StoreServiceClient.StoreItemView item) {
        String status = item.getWindowStatus() != null
                ? item.getWindowStatus().trim().toUpperCase(Locale.ROOT)
                : null;
        if (status == null && item.getWithinClaimWindow() != null) {
            if (!item.getWithinClaimWindow()) {
                throw new AppException(
                        "Outside claim window",
                        "This item is not available to claim right now",
                        "CLAIM_WINDOW",
                        HttpStatus.BAD_REQUEST);
            }
            return;
        }
        if ("NOT_OPEN".equals(status)) {
            throw new AppException(
                    "Claim window not open",
                    "This item is not open for claiming yet",
                    "CLAIM_NOT_OPEN",
                    HttpStatus.BAD_REQUEST);
        }
        if ("EXPIRED".equals(status)) {
            throw new AppException(
                    "Claim window expired",
                    "This item's claim window has expired",
                    "CLAIM_EXPIRED",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private String resolveStudentName(UUID studentId) {
        try {
            ResponseEntity<CommonResponseDto<AuthServiceClient.UserNameView>> response =
                    authServiceClient.getUser(studentId);
            if (response.getBody() != null && response.getBody().getData() != null) {
                AuthServiceClient.UserNameView u = response.getBody().getData();
                if (u.getName() != null && !u.getName().isBlank()) {
                    return u.getName();
                }
                return u.getUsername();
            }
        } catch (Exception ex) {
            log.warn("Could not resolve student name for {}: {}", studentId, ex.getMessage());
        }
        return null;
    }

    private void ensureTasksCompleted(UUID studentId, List<UUID> requiredTasks) {
        if (requiredTasks == null || requiredTasks.isEmpty()) {
            throw new TasksNotCompletedException("This item requires completed tasks, but none are linked");
        }
        try {
            ResponseEntity<CommonResponseDto<TaskServiceClient.CompletionCheckResponse>> response =
                    taskServiceClient.checkCompletion(TaskServiceClient.CompletionCheckRequest.builder()
                            .studentId(studentId)
                            .taskIds(requiredTasks)
                            .build());
            if (response.getBody() == null || response.getBody().getData() == null
                    || !response.getBody().getData().isAllCompleted()) {
                throw new TasksNotCompletedException(
                        "Complete all required tasks before claiming this item");
            }
        } catch (TasksNotCompletedException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Task completion check failed: {}", ex.getMessage(), ex);
            throw new AppException(
                    "Could not verify task completion",
                    "Could not verify required tasks. Try again shortly.",
                    "TASK_CHECK_FAILED",
                    HttpStatus.BAD_GATEWAY);
        }
    }

    public List<TransactionDto> getTransactionHistory(UUID studentId) {
        return TransactionMapper.toDtoList(
            transactionRepository.findByStudentIdOrderByCreatedAtDesc(studentId)
        );
    }

    public List<WalletDto> getLeaderboard(int limit) {
        return walletRepository.findAllByOrderByBalanceDesc().stream()
                .limit(limit)
                .map(WalletMapper::toDto)
                .toList();
    }
}
