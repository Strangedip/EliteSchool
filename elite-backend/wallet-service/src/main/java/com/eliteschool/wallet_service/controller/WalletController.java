package com.eliteschool.wallet_service.controller;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.common_utils.util.ResponseUtil;
import com.eliteschool.wallet_service.dto.TransactionDto;
import com.eliteschool.wallet_service.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

    private final WalletService walletService;

    /**
     * Get a student's wallet balance.
     * @param studentId The student's ID.
     * @return Wallet balance.
     */
    @GetMapping("/{studentId}/balance")
    public ResponseEntity<CommonResponseDto<Integer>> getWalletBalance(@PathVariable UUID studentId) {
        log.info("Request received to get wallet balance for student: {}", studentId);
        int balance = walletService.getWalletBalance(studentId);
        return ResponseUtil.success("Wallet balance retrieved successfully", balance);
    }

    /**
     * Add points to wallet (credit operation).
     * @param studentId The student's ID.
     * @param points The points to add.
     * @param description The reason for adding points.
     * @return ResponseEntity with success message and new balance.
     */
    @PostMapping("/{studentId}/credit")
    public ResponseEntity<CommonResponseDto<Integer>> creditPoints(
            @PathVariable UUID studentId,
            @RequestParam int points,
            @RequestParam String description) {
        
        log.info("Request received to credit {} points for student: {} with description: {}", 
                points, studentId, description);
        
        walletService.creditPoints(studentId, points, description);
        int newBalance = walletService.getWalletBalance(studentId);
        return ResponseUtil.success("Points added successfully", newBalance);
    }

    /**
     * Award points for task completion (internal service endpoint)
     * @param request The award points request.
     * @return ResponseEntity with success message.
     */
    @PostMapping("/award")
    public ResponseEntity<CommonResponseDto<Void>> awardTaskPoints(@Valid @RequestBody AwardPointsRequest request) {
        log.info("Request received to award {} points to student: {} for: {}", 
                request.points(), request.studentId(), request.description());
        
        walletService.creditPoints(request.studentId(), request.points(), request.description());
        return ResponseUtil.success("Points awarded successfully", null);
    }

    /**
     * Deduct points from wallet (debit operation).
     * @param studentId The student's ID.
     * @param points The points to deduct.
     * @param description The reason for deducting points.
     * @return ResponseEntity with success message and new balance.
     */
    @PostMapping("/{studentId}/debit")
    public ResponseEntity<CommonResponseDto<Integer>> debitPoints(
            @PathVariable UUID studentId,
            @RequestParam int points,
            @RequestParam String description) {
        
        log.info("Request received to debit {} points from student: {} with description: {}", 
                points, studentId, description);
        
        walletService.debitPoints(studentId, points, description);
        int newBalance = walletService.getWalletBalance(studentId);
        return ResponseUtil.success("Points deducted successfully", newBalance);
    }

    /**
     * Purchase an item from the store using wallet points.
     * @param studentId The student's ID.
     * @param itemId The item ID to purchase.
     * @return ResponseEntity with detailed response.
     */
    @PostMapping("/{studentId}/purchase/{itemId}")
    public ResponseEntity<CommonResponseDto<PurchaseResponse>> purchaseItem(
            @PathVariable UUID studentId, 
            @PathVariable UUID itemId) {
        
        log.info("Request received to purchase item: {} for student: {}", itemId, studentId);
        
        boolean success = walletService.purchaseItem(studentId, itemId);
        int remainingBalance = walletService.getWalletBalance(studentId);
        
        PurchaseResponse response = new PurchaseResponse(
            true,
            "Item purchased successfully!",
            remainingBalance
        );
        return ResponseUtil.success("Item purchased successfully", response);
    }

    /**
     * Get a student's transaction history.
     * @param studentId The student's ID.
     * @return List of transactions.
     */
    @GetMapping("/{studentId}/transactions")
    public ResponseEntity<CommonResponseDto<List<TransactionDto>>> getTransactionHistory(@PathVariable UUID studentId) {
        log.info("Request received to get transaction history for student: {}", studentId);
        List<TransactionDto> transactions = walletService.getTransactionHistory(studentId);
        return ResponseUtil.success("Transaction history retrieved successfully", transactions);
    }
    
    // Request record for task completion points
    private record AwardPointsRequest(
            UUID studentId, 
            int points, 
            String description) {}
    
    // Response record for purchase operations
    public record PurchaseResponse(
            boolean success, 
            String message, 
            int remainingBalance) {}
}
