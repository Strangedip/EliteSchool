package com.eliteschool.reward_service.service;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.reward_service.client.StoreServiceClient;
import com.eliteschool.reward_service.dto.RewardTransactionDto;
import com.eliteschool.reward_service.dto.RewardTransactionMapper;
import com.eliteschool.reward_service.exception.InsufficientPointsException;
import com.eliteschool.reward_service.exception.ItemOutOfStockException;
import com.eliteschool.reward_service.model.RewardTransaction;
import com.eliteschool.reward_service.model.RewardWallet;
import com.eliteschool.reward_service.model.RedeemableItem;
import com.eliteschool.reward_service.model.enums.TransactionType;
import com.eliteschool.reward_service.repository.RewardTransactionRepository;
import com.eliteschool.reward_service.repository.RewardWalletRepository;
import com.eliteschool.reward_service.repository.RedeemableItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardService {

    private final RewardWalletRepository rewardWalletRepository;
    private final RewardTransactionRepository rewardTransactionRepository;
    private final RedeemableItemRepository redeemableItemRepository;
    private final StoreServiceClient storeServiceClient;

    /**
     * Get the wallet balance of a student.
     * @param studentId The student's ID.
     * @return Wallet balance (default 0 if not found).
     */
    public int getWalletBalance(UUID studentId) {
        log.info("Getting wallet balance for student: {}", studentId);
        return rewardWalletRepository.findByStudentId(studentId)
                .map(RewardWallet::getBalance)
                .orElse(0);
    }

    /**
     * Earn reward points (e.g., when completing a task).
     * @param studentId The student's ID.
     * @param points The points earned.
     * @param description The reason (e.g., "Task Completed").
     */
    @Transactional
    public void earnPoints(UUID studentId, int points, String description) {
        log.info("Adding {} points to student {} wallet for: {}", points, studentId, description);
        
        RewardWallet wallet = rewardWalletRepository.findByStudentId(studentId)
                .orElse(new RewardWallet(studentId, 0));

        wallet.setBalance(wallet.getBalance() + points);
        rewardWalletRepository.save(wallet);

        RewardTransaction transaction = RewardTransaction.builder()
                .studentId(studentId)
                .transactionType(TransactionType.EARNED)
                .points(points)
                .description(description)
                .build();

        rewardTransactionRepository.save(transaction);
        
        log.info("Student {} now has a balance of {} points", studentId, wallet.getBalance());
    }

    /**
     * Spend reward points to redeem an item.
     * This method integrates with the Store Service to ensure stock is updated.
     * 
     * @param studentId The student's ID.
     * @param itemId The item ID to be redeemed.
     * @return True if redeemed successfully, otherwise false.
     * @throws InsufficientPointsException if student doesn't have enough points
     * @throws ItemOutOfStockException if item is out of stock
     * @throws EntityNotFoundException if wallet or item is not found
     */
    @Transactional
    public boolean spendPoints(UUID studentId, UUID itemId) {
        log.info("Starting redemption process for student {} and item {}", studentId, itemId);
        
        // 1. Get the student's wallet
        RewardWallet wallet = rewardWalletRepository.findByStudentId(studentId)
                .orElseThrow(() -> {
                    log.warn("Wallet not found for student: {}", studentId);
                    return new EntityNotFoundException("Wallet not found for student: " + studentId);
                });
        
        // 2. Get the item details
        RedeemableItem item = redeemableItemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Item not found: {}", itemId);
                    return new EntityNotFoundException("Item not found: " + itemId);
                });
        
        // 3. Check if the student has enough points
        if (wallet.getBalance() < item.getPrice()) {
            log.warn("Insufficient points for student {}: has {} but needs {}", 
                    studentId, wallet.getBalance(), item.getPrice());
            throw new InsufficientPointsException(
                    "Insufficient points: required " + item.getPrice() + 
                    ", available " + wallet.getBalance());
        }
        
        try {
            // 4. Call the store service to update stock
            ResponseEntity<CommonResponseDto<Object>> storeResponse = storeServiceClient.purchaseItem(itemId);
            
            if (!storeResponse.getStatusCode().is2xxSuccessful() || 
                storeResponse.getBody() == null || 
                !storeResponse.getBody().isSuccess()) {
                log.error("Failed to update item stock in store service: {}", 
                    storeResponse.getBody() != null ? storeResponse.getBody().getMessage() : "Unknown error");
                throw new ItemOutOfStockException("Item is out of stock or unavailable in the store");
            }
            
            // 5. Deduct points from wallet
            wallet.setBalance(wallet.getBalance() - item.getPrice());
            rewardWalletRepository.save(wallet);
            
            // 6. Record the transaction
            RewardTransaction transaction = RewardTransaction.builder()
                    .studentId(studentId)
                    .transactionType(TransactionType.SPENT)
                    .points(item.getPrice())
                    .description("Redeemed: " + item.getName())
                    .build();
            
            rewardTransactionRepository.save(transaction);
            
            log.info("Successfully completed redemption for student {} and item {}", studentId, itemId);
            return true;
            
        } catch (ItemOutOfStockException | InsufficientPointsException | EntityNotFoundException e) {
            // Re-throw these exceptions so they can be handled by the exception handler
            throw e;
        } catch (Exception e) {
            log.error("Error during item redemption process", e);
            throw new RuntimeException("Failed to complete redemption process", e);
        }
    }

    /**
     * Get a student's transaction history.
     * @param studentId The student's ID.
     * @return List of transaction DTOs.
     */
    public List<RewardTransactionDto> getTransactionHistory(UUID studentId) {
        log.info("Getting transaction history for student: {}", studentId);
        List<RewardTransaction> transactions = rewardTransactionRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
        return RewardTransactionMapper.toDtoList(transactions);
    }
}
