package com.eliteschool.wallet_service.controller;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.common_utils.security.GatewayAuth;
import com.eliteschool.common_utils.util.ResponseUtil;
import com.eliteschool.wallet_service.dto.NominationDto;
import com.eliteschool.wallet_service.model.enums.NominationStatus;
import com.eliteschool.wallet_service.service.NominationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallet/nominations")
@RequiredArgsConstructor
public class NominationController {

    private final NominationService nominationService;

    @PostMapping
    public ResponseEntity<CommonResponseDto<NominationDto>> create(
            @Valid @RequestBody NominationDto body,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "FACULTY", "MANAGEMENT", "ADMIN");
        UUID nominatorId = GatewayAuth.requireUserId(request);
        String role = GatewayAuth.role(request);
        return ResponseUtil.success("Nomination created",
                nominationService.create(body, nominatorId, role));
    }

    @GetMapping
    public ResponseEntity<CommonResponseDto<List<NominationDto>>> list(
            @RequestParam(required = false) NominationStatus status,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "FACULTY", "MANAGEMENT", "ADMIN");
        UUID userId = GatewayAuth.requireUserId(request);
        String role = GatewayAuth.role(request);
        return ResponseUtil.success("Nominations retrieved",
                nominationService.listForCaller(role, userId, status));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<CommonResponseDto<NominationDto>> approve(
            @PathVariable UUID id,
            @RequestParam(required = false) Integer points,
            @RequestParam(required = false) String reviewNotes,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "ADMIN");
        UUID adminId = GatewayAuth.requireUserId(request);
        return ResponseUtil.success("Nomination approved",
                nominationService.approve(id, adminId, points, reviewNotes));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<CommonResponseDto<NominationDto>> reject(
            @PathVariable UUID id,
            @RequestParam(required = false) String reviewNotes,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "ADMIN");
        UUID adminId = GatewayAuth.requireUserId(request);
        return ResponseUtil.success("Nomination rejected",
                nominationService.reject(id, adminId, reviewNotes));
    }
}
