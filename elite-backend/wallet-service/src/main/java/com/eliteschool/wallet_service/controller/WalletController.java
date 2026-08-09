package com.eliteschool.wallet_service.controller;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.common_utils.security.GatewayAuth;
import com.eliteschool.common_utils.util.ResponseUtil;
import com.eliteschool.wallet_service.dto.TransactionDto;
import com.eliteschool.wallet_service.dto.WalletDto;
import com.eliteschool.wallet_service.model.enums.TransactionSource;
import com.eliteschool.wallet_service.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/{studentId}/balance")
    public ResponseEntity<CommonResponseDto<Integer>> getWalletBalance(@PathVariable UUID studentId,
                                                                       HttpServletRequest request) {
        GatewayAuth.requireSelfOrRoles(request, studentId, "ADMIN", "MANAGEMENT", "FACULTY");
        return ResponseUtil.success("Balance retrieved", walletService.getWalletBalance(studentId));
    }

    @PostMapping("/{studentId}/credit")
    public ResponseEntity<CommonResponseDto<Integer>> creditPoints(
            @PathVariable UUID studentId,
            @RequestParam int points,
            @RequestParam String description,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "ADMIN", "MANAGEMENT");
        walletService.creditPoints(studentId, points, description, null, TransactionSource.ADMIN_ADJUSTMENT);
        return ResponseUtil.success("Points credited", walletService.getWalletBalance(studentId));
    }

    @PostMapping("/award")
    public ResponseEntity<CommonResponseDto<Void>> awardTaskPoints(@Valid @RequestBody AwardPointsRequest requestBody,
                                                                   HttpServletRequest request) {
        GatewayAuth.requireInternal(request);
        walletService.creditPoints(
                requestBody.studentId(),
                requestBody.points(),
                requestBody.description(),
                requestBody.referenceId(),
                TransactionSource.TASK_REWARD);
        return ResponseUtil.success("Points awarded", null);
    }

    @PostMapping("/{studentId}/debit")
    public ResponseEntity<CommonResponseDto<Integer>> debitPoints(
            @PathVariable UUID studentId,
            @RequestParam int points,
            @RequestParam String description,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "ADMIN", "MANAGEMENT");
        walletService.debitPoints(studentId, points, description, TransactionSource.ADMIN_ADJUSTMENT);
        return ResponseUtil.success("Points debited", walletService.getWalletBalance(studentId));
    }

    @PostMapping("/{studentId}/purchase/{itemId}")
    public ResponseEntity<CommonResponseDto<PurchaseResponse>> purchaseItem(
            @PathVariable UUID studentId,
            @PathVariable UUID itemId,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "STUDENT");
        UUID callerId = GatewayAuth.requireUserId(request);
        if (!callerId.equals(studentId)) {
            throw new com.eliteschool.common_utils.exception.AppException(
                    "Cannot purchase for another student",
                    "You can only purchase items for your own wallet",
                    "FORBIDDEN",
                    org.springframework.http.HttpStatus.FORBIDDEN);
        }
        walletService.purchaseItem(studentId, itemId);
        int remainingBalance = walletService.getWalletBalance(studentId);
        return ResponseUtil.success("Purchase successful",
            new PurchaseResponse(true, "Item purchased!", remainingBalance));
    }

    @GetMapping("/{studentId}/transactions")
    public ResponseEntity<CommonResponseDto<List<TransactionDto>>> getTransactionHistory(
            @PathVariable UUID studentId,
            HttpServletRequest request) {
        GatewayAuth.requireSelfOrRoles(request, studentId, "ADMIN", "MANAGEMENT", "FACULTY");
        return ResponseUtil.success("Transactions retrieved", walletService.getTransactionHistory(studentId));
    }

    @GetMapping("/admin-adjustments")
    public ResponseEntity<CommonResponseDto<List<TransactionDto>>> getAdminAdjustments(
            @RequestParam(defaultValue = "100") int limit,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "ADMIN", "MANAGEMENT");
        return ResponseUtil.success("Admin adjustments retrieved", walletService.getAdminAdjustments(limit));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<CommonResponseDto<List<WalletDto>>> getLeaderboard(
            @RequestParam(defaultValue = "10") int limit,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "ADMIN", "MANAGEMENT", "FACULTY", "STUDENT");
        return ResponseUtil.success("Leaderboard retrieved", walletService.getLeaderboard(limit));
    }

    private record AwardPointsRequest(UUID studentId, int points, String description, String referenceId) {}

    public record PurchaseResponse(boolean success, String message, int remainingBalance) {}
}
