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

    @GetMapping("/{studentId}/balance")
    public ResponseEntity<CommonResponseDto<Integer>> getWalletBalance(@PathVariable UUID studentId) {
        return ResponseUtil.success("Balance retrieved", walletService.getWalletBalance(studentId));
    }

    @PostMapping("/{studentId}/credit")
    public ResponseEntity<CommonResponseDto<Integer>> creditPoints(
            @PathVariable UUID studentId,
            @RequestParam int points,
            @RequestParam String description) {
        walletService.creditPoints(studentId, points, description);
        return ResponseUtil.success("Points credited", walletService.getWalletBalance(studentId));
    }

    // Internal endpoint for task-service to award points on task completion
    @PostMapping("/award")
    public ResponseEntity<CommonResponseDto<Void>> awardTaskPoints(@Valid @RequestBody AwardPointsRequest request) {
        walletService.creditPoints(request.studentId(), request.points(), request.description());
        return ResponseUtil.success("Points awarded", null);
    }

    @PostMapping("/{studentId}/debit")
    public ResponseEntity<CommonResponseDto<Integer>> debitPoints(
            @PathVariable UUID studentId,
            @RequestParam int points,
            @RequestParam String description) {
        walletService.debitPoints(studentId, points, description);
        return ResponseUtil.success("Points debited", walletService.getWalletBalance(studentId));
    }

    @PostMapping("/{studentId}/purchase/{itemId}")
    public ResponseEntity<CommonResponseDto<PurchaseResponse>> purchaseItem(
            @PathVariable UUID studentId,
            @PathVariable UUID itemId) {
        log.info("Purchase request: student={}, item={}", studentId, itemId);
        walletService.purchaseItem(studentId, itemId);
        int remainingBalance = walletService.getWalletBalance(studentId);
        return ResponseUtil.success("Purchase successful", 
            new PurchaseResponse(true, "Item purchased!", remainingBalance));
    }

    @GetMapping("/{studentId}/transactions")
    public ResponseEntity<CommonResponseDto<List<TransactionDto>>> getTransactionHistory(@PathVariable UUID studentId) {
        return ResponseUtil.success("Transactions retrieved", walletService.getTransactionHistory(studentId));
    }

    private record AwardPointsRequest(UUID studentId, int points, String description) {}

    public record PurchaseResponse(boolean success, String message, int remainingBalance) {}
}
