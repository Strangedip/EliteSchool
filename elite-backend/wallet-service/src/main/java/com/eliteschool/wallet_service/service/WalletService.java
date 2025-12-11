package com.eliteschool.wallet_service.service;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.wallet_service.client.StoreServiceClient;
import com.eliteschool.wallet_service.dto.TransactionDto;
import com.eliteschool.wallet_service.dto.TransactionMapper;
import com.eliteschool.wallet_service.exception.InsufficientPointsException;
import com.eliteschool.wallet_service.exception.ItemOutOfStockException;
import com.eliteschool.wallet_service.model.Transaction;
import com.eliteschool.wallet_service.model.Wallet;
import com.eliteschool.wallet_service.model.enums.TransactionType;
import com.eliteschool.wallet_service.repository.TransactionRepository;
import com.eliteschool.wallet_service.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final StoreServiceClient storeServiceClient;

    public int getWalletBalance(UUID studentId) {
        return walletRepository.findByStudentId(studentId)
                .map(Wallet::getBalance)
                .orElse(0);
    }

    @Transactional
    public void creditPoints(UUID studentId, int points, String description) {
        Wallet wallet = walletRepository.findByStudentId(studentId)
                .orElse(new Wallet(studentId, 0));

        wallet.setBalance(wallet.getBalance() + points);
        walletRepository.save(wallet);

        transactionRepository.save(Transaction.builder()
                .studentId(studentId)
                .transactionType(TransactionType.CREDIT)
                .points(points)
                .description(description)
                .build());
    }

    @Transactional
    public boolean debitPoints(UUID studentId, int points, String description) {
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

    // Purchases item from store - deducts points and updates store stock via Feign client
    @Transactional
    public boolean purchaseItem(UUID studentId, UUID itemId) {
        log.info("Processing purchase: student={}, item={}", studentId, itemId);

        // Get item price from store service
        ResponseEntity<CommonResponseDto<Object>> itemResponse = storeServiceClient.getItemPrice(itemId);
        if (!itemResponse.getStatusCode().is2xxSuccessful() || 
            itemResponse.getBody() == null || 
            !itemResponse.getBody().isSuccess()) {
            throw new EntityNotFoundException("Item not found: " + itemId);
        }

        int itemPrice = (int) itemResponse.getBody().getData();

        // Deduct points then update store stock
        debitPoints(studentId, itemPrice, "Purchase: Item #" + itemId);

        ResponseEntity<CommonResponseDto<Object>> storeResponse = storeServiceClient.purchaseItem(itemId);
        if (!storeResponse.getStatusCode().is2xxSuccessful() || 
            storeResponse.getBody() == null || 
            !storeResponse.getBody().isSuccess()) {
            throw new ItemOutOfStockException("Item out of stock or unavailable");
        }

        return true;
    }

    public List<TransactionDto> getTransactionHistory(UUID studentId) {
        return TransactionMapper.toDtoList(
            transactionRepository.findByStudentIdOrderByCreatedAtDesc(studentId)
        );
    }
}
