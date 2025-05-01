package com.eliteschool.reward_service.controller;

import com.eliteschool.reward_service.model.RewardTransaction;
import com.eliteschool.reward_service.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    /**
     * Get a student's reward wallet balance.
     * @param studentId The student's ID.
     * @return Wallet balance.
     */
    @GetMapping("/wallet/{studentId}")
    public ResponseEntity<Integer> getWalletBalance(@PathVariable UUID studentId) {
        return ResponseEntity.ok(rewardService.getWalletBalance(studentId));
    }

    /**
     * Earn reward points (e.g., when completing a task).
     * @param studentId The student's ID.
     * @param points The points to add.
     * @param description The reason for earning points.
     * @return ResponseEntity with success message.
     */
    @PostMapping("/earn/{studentId}")
    public ResponseEntity<String> earnPoints(@PathVariable UUID studentId,
                                             @RequestParam int points,
                                             @RequestParam String description) {
        rewardService.earnPoints(studentId, points, description);
        return ResponseEntity.ok("Points added successfully!");
    }

    /**
     * Award points for task completion (internal service endpoint)
     */
    @PostMapping("/award")
    public ResponseEntity<Void> awardTaskPoints(@RequestBody AwardPointsRequest request) {
        rewardService.earnPoints(request.studentId(), request.points(), request.description());
        return ResponseEntity.ok().build();
    }

    /**
     * Spend reward points to redeem an item.
     * @param studentId The student's ID.
     * @param itemId The item ID to redeem.
     * @return ResponseEntity with success or failure message.
     */
    @PostMapping("/spend/{studentId}/{itemId}")
    public ResponseEntity<String> spendPoints(@PathVariable UUID studentId, @PathVariable UUID itemId) {
        boolean success = rewardService.spendPoints(studentId, itemId);
        return success ? ResponseEntity.ok("Item redeemed successfully!")
                : ResponseEntity.badRequest().body("Insufficient points or item out of stock.");
    }

    /**
     * Get a student's transaction history.
     * @param studentId The student's ID.
     * @return List of reward transactions.
     */
    @GetMapping("/transactions/{studentId}")
    public ResponseEntity<List<RewardTransaction>> getTransactionHistory(@PathVariable UUID studentId) {
        return ResponseEntity.ok(rewardService.getTransactionHistory(studentId));
    }
    
    // Request record for task completion points
    private record AwardPointsRequest(UUID studentId, int points, String description) {}
}
