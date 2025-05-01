package com.eliteschool.reward_service.controller;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.common_utils.exception.AppException;
import com.eliteschool.common_utils.util.ResponseUtil;
import com.eliteschool.reward_service.dto.RewardTransactionDto;
import com.eliteschool.reward_service.service.RewardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
@Slf4j
public class RewardController {

    private final RewardService rewardService;

    /**
     * Get a student's reward wallet balance.
     * @param studentId The student's ID.
     * @return Wallet balance.
     */
    @GetMapping("/wallet/{studentId}")
    public ResponseEntity<CommonResponseDto<Integer>> getWalletBalance(@PathVariable UUID studentId) {
        log.info("Request received to get wallet balance for student: {}", studentId);
        int balance = rewardService.getWalletBalance(studentId);
        return ResponseUtil.success("Wallet balance retrieved successfully", balance);
    }

    /**
     * Earn reward points (e.g., when completing a task).
     * @param studentId The student's ID.
     * @param points The points to add.
     * @param description The reason for earning points.
     * @return ResponseEntity with success message.
     */
    @PostMapping("/earn/{studentId}")
    public ResponseEntity<CommonResponseDto<Integer>> earnPoints(
            @PathVariable UUID studentId,
            @RequestParam int points,
            @RequestParam String description) {
        
        log.info("Request received to earn {} points for student: {} with description: {}", 
                points, studentId, description);
        
        rewardService.earnPoints(studentId, points, description);
        int newBalance = rewardService.getWalletBalance(studentId);
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
        
        rewardService.earnPoints(request.studentId(), request.points(), request.description());
        return ResponseUtil.success("Points awarded successfully", null);
    }

    /**
     * Spend reward points to redeem an item.
     * @param studentId The student's ID.
     * @param itemId The item ID to redeem.
     * @return ResponseEntity with detailed response.
     */
    @PostMapping("/spend/{studentId}/{itemId}")
    public ResponseEntity<CommonResponseDto<RedemptionResponse>> spendPoints(
            @PathVariable UUID studentId, 
            @PathVariable UUID itemId) {
        
        log.info("Request received to redeem item: {} for student: {}", itemId, studentId);
        
        boolean success = rewardService.spendPoints(studentId, itemId);
        int remainingBalance = rewardService.getWalletBalance(studentId);
        
        RedemptionResponse response = new RedemptionResponse(
            true,
            "Item redeemed successfully!",
            remainingBalance
        );
        return ResponseUtil.success("Item redeemed successfully", response);
    }

    /**
     * Get a student's transaction history.
     * @param studentId The student's ID.
     * @return List of reward transactions.
     */
    @GetMapping("/transactions/{studentId}")
    public ResponseEntity<CommonResponseDto<List<RewardTransactionDto>>> getTransactionHistory(@PathVariable UUID studentId) {
        log.info("Request received to get transaction history for student: {}", studentId);
        List<RewardTransactionDto> transactions = rewardService.getTransactionHistory(studentId);
        return ResponseUtil.success("Transaction history retrieved successfully", transactions);
    }
    
    // Request record for task completion points
    private record AwardPointsRequest(
            UUID studentId, 
            int points, 
            String description) {}
    
    // Response record for redemption operations
    public record RedemptionResponse(
            boolean success, 
            String message, 
            int remainingBalance) {}
}
