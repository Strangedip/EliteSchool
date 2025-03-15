package com.eliteschool.reward_service.service;

import com.eliteschool.reward_service.model.RewardTransaction;
import com.eliteschool.reward_service.model.RewardWallet;
import com.eliteschool.reward_service.model.RedeemableItem;
import com.eliteschool.reward_service.model.enums.TransactionType;
import com.eliteschool.reward_service.repository.RewardTransactionRepository;
import com.eliteschool.reward_service.repository.RewardWalletRepository;
import com.eliteschool.reward_service.repository.RedeemableItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RewardService {

    private final RewardWalletRepository rewardWalletRepository;
    private final RewardTransactionRepository rewardTransactionRepository;
    private final RedeemableItemRepository redeemableItemRepository;


    @Autowired
    public RewardService(RewardWalletRepository rewardWalletRepository, RewardTransactionRepository rewardTransactionRepository, RedeemableItemRepository redeemableItemRepository) {
        this.rewardWalletRepository = rewardWalletRepository;
        this.rewardTransactionRepository = rewardTransactionRepository;
        this.redeemableItemRepository = redeemableItemRepository;
    }
    /**
     * Get the wallet balance of a student.
     * @param studentId The student's ID.
     * @return Wallet balance (default 0 if not found).
     */
    public int getWalletBalance(UUID studentId) {
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
    }

    /**
     * Spend reward points to redeem an item.
     * @param studentId The student's ID.
     * @param itemId The item ID to be redeemed.
     * @return True if redeemed successfully, otherwise false.
     */
    @Transactional
    public boolean spendPoints(UUID studentId, UUID itemId) {
        Optional<RewardWallet> walletOpt = rewardWalletRepository.findByStudentId(studentId);
        Optional<RedeemableItem> itemOpt = redeemableItemRepository.findById(itemId);

        if (walletOpt.isEmpty() || itemOpt.isEmpty()) return false;

        RewardWallet wallet = walletOpt.get();
        RedeemableItem item = itemOpt.get();

        if (wallet.getBalance() < item.getPrice() || item.getStock() <= 0) {
            return false; // Not enough points or out of stock
        }

        wallet.setBalance(wallet.getBalance() - item.getPrice());
        item.setStock(item.getStock() - 1);

        rewardWalletRepository.save(wallet);
        redeemableItemRepository.save(item);

        RewardTransaction transaction = RewardTransaction.builder()
                .studentId(studentId)
                .transactionType(TransactionType.SPENT)
                .points(item.getPrice())
                .description("Redeemed " + item.getName())
                .build();

        rewardTransactionRepository.save(transaction);
        return true;
    }

    /**
     * Get a student's transaction history.
     * @param studentId The student's ID.
     * @return List of transactions.
     */
    public List<RewardTransaction> getTransactionHistory(UUID studentId) {
        return rewardTransactionRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }
}
