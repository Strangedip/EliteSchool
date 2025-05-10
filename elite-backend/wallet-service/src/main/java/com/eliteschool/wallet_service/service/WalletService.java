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

    /**
     * Get the wallet balance of a student.
     * @param studentId The student's ID.
     * @return Wallet balance (default 0 if not found).
     */
    public int getWalletBalance(UUID studentId) {
        log.info("Getting wallet balance for student: {}", studentId);
        return walletRepository.findByStudentId(studentId)
                .map(Wallet::getBalance)
                .orElse(0);
    }

    /**
     * Add points to wallet (credit operation).
     * @param studentId The student's ID.
     * @param points The points to add.
     * @param description The reason for adding points.
     */
    @Transactional
    public void creditPoints(UUID studentId, int points, String description) {
        log.info("Adding {} points to student {} wallet for: {}", points, studentId, description);
        
        Wallet wallet = walletRepository.findByStudentId(studentId)
                .orElse(new Wallet(studentId, 0));

        wallet.setBalance(wallet.getBalance() + points);
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .studentId(studentId)
                .transactionType(TransactionType.CREDIT)
                .points(points)
                .description(description)
                .build();

        transactionRepository.save(transaction);
        
        log.info("Student {} now has a balance of {} points", studentId, wallet.getBalance());
    }

    /**
     * Deduct points from wallet (debit operation).
     * This method integrates with the Store Service to ensure stock is updated when purchasing items.
     * 
     * @param studentId The student's ID.
     * @param points The points to deduct.
     * @param description The reason for deducting points.
     * @return True if deducted successfully, otherwise false.
     * @throws InsufficientPointsException if student doesn't have enough points
     * @throws EntityNotFoundException if wallet is not found
     */
    @Transactional
    public boolean debitPoints(UUID studentId, int points, String description) {
        log.info("Deducting {} points from student {} wallet for: {}", points, studentId, description);
        
        // 1. Get the student's wallet
        Wallet wallet = walletRepository.findByStudentId(studentId)
                .orElseThrow(() -> {
                    log.warn("Wallet not found for student: {}", studentId);
                    return new EntityNotFoundException("Wallet not found for student: " + studentId);
                });
        
        // 2. Check if the student has enough points
        if (wallet.getBalance() < points) {
            log.warn("Insufficient points for student {}: has {} but needs {}", 
                    studentId, wallet.getBalance(), points);
            throw new InsufficientPointsException(
                    "Insufficient points: required " + points + 
                    ", available " + wallet.getBalance());
        }
        
        // 3. Deduct points from wallet
        wallet.setBalance(wallet.getBalance() - points);
        walletRepository.save(wallet);
        
        // 4. Record the transaction
        Transaction transaction = Transaction.builder()
                .studentId(studentId)
                .transactionType(TransactionType.DEBIT)
                .points(points)
                .description(description)
                .build();
        
        transactionRepository.save(transaction);
        
        log.info("Successfully deducted points. Student {} now has {} points", studentId, wallet.getBalance());
        return true;
    }
    
    /**
     * Purchase an item from the store using wallet points.
     * This method integrates with the Store Service to ensure stock is updated.
     * 
     * @param studentId The student's ID.
     * @param itemId The item ID to be purchased.
     * @return True if purchased successfully, otherwise false.
     * @throws InsufficientPointsException if student doesn't have enough points
     * @throws ItemOutOfStockException if item is out of stock
     * @throws EntityNotFoundException if wallet is not found
     */
    @Transactional
    public boolean purchaseItem(UUID studentId, UUID itemId) {
        log.info("Starting purchase process for student {} and item {}", studentId, itemId);
        
        // 1. Get the item price from store service
        ResponseEntity<CommonResponseDto<Object>> itemResponse = storeServiceClient.getItemPrice(itemId);
        if (!itemResponse.getStatusCode().is2xxSuccessful() || 
            itemResponse.getBody() == null || 
            !itemResponse.getBody().isSuccess()) {
            log.error("Failed to get item price from store service");
            throw new EntityNotFoundException("Item not found: " + itemId);
        }
        
        // Extract item details
        int itemPrice = (int) itemResponse.getBody().getData();
        
        // 2. Deduct points from wallet
        try {
            debitPoints(studentId, itemPrice, "Purchase: Item #" + itemId);
            
            // 3. Update item stock in store service
            ResponseEntity<CommonResponseDto<Object>> storeResponse = storeServiceClient.purchaseItem(itemId);
            
            if (!storeResponse.getStatusCode().is2xxSuccessful() || 
                storeResponse.getBody() == null || 
                !storeResponse.getBody().isSuccess()) {
                log.error("Failed to update item stock in store service: {}", 
                    storeResponse.getBody() != null ? storeResponse.getBody().getMessage() : "Unknown error");
                throw new ItemOutOfStockException("Item is out of stock or unavailable in the store");
            }
            
            log.info("Successfully completed purchase for student {} and item {}", studentId, itemId);
            return true;
            
        } catch (InsufficientPointsException | ItemOutOfStockException | EntityNotFoundException e) {
            // Re-throw these exceptions so they can be handled by the exception handler
            throw e;
        } catch (Exception e) {
            log.error("Error during item purchase process", e);
            throw new RuntimeException("Failed to complete purchase process", e);
        }
    }

    /**
     * Get a student's transaction history.
     * @param studentId The student's ID.
     * @return List of transaction DTOs.
     */
    public List<TransactionDto> getTransactionHistory(UUID studentId) {
        log.info("Getting transaction history for student: {}", studentId);
        List<Transaction> transactions = transactionRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
        return TransactionMapper.toDtoList(transactions);
    }
}
