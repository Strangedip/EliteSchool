package com.eliteschool.auth_service.controller;

import com.eliteschool.auth_service.dto.SupportMessageDto;
import com.eliteschool.auth_service.dto.SupportTicketDto;
import com.eliteschool.auth_service.dto.request.UpdateSupportStatusRequest;
import com.eliteschool.auth_service.model.enums.SupportTicketStatus;
import com.eliteschool.auth_service.service.SupportService;
import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.common_utils.security.GatewayAuth;
import com.eliteschool.common_utils.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private static final String[] STAFF_ROLES = {"FACULTY", "ADMIN", "MANAGEMENT"};
    private static final String[] ALL_ROLES = {"STUDENT", "FACULTY", "ADMIN", "MANAGEMENT"};

    private final SupportService supportService;

    @PostMapping("/tickets")
    public ResponseEntity<CommonResponseDto<SupportTicketDto>> createTicket(
            @Valid @RequestBody SupportTicketDto dto,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "STUDENT");
        UUID studentId = GatewayAuth.requireUserId(request);
        return ResponseUtil.success("Support ticket created", supportService.createTicket(studentId, dto));
    }

    @GetMapping("/tickets/mine")
    public ResponseEntity<CommonResponseDto<List<SupportTicketDto>>> getMyTickets(HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "STUDENT");
        UUID studentId = GatewayAuth.requireUserId(request);
        return ResponseUtil.success("Your support tickets retrieved", supportService.getMyTickets(studentId));
    }

    @GetMapping("/tickets")
    public ResponseEntity<CommonResponseDto<List<SupportTicketDto>>> getAllTickets(
            @RequestParam(required = false) SupportTicketStatus status,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, STAFF_ROLES);
        return ResponseUtil.success("Support tickets retrieved", supportService.getAllTickets(status));
    }

    @GetMapping("/tickets/{id}")
    public ResponseEntity<CommonResponseDto<SupportTicketDto>> getTicket(
            @PathVariable UUID id,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, ALL_ROLES);
        SupportTicketDto ticket = supportService.getTicket(id);
        GatewayAuth.requireSelfOrRoles(request, ticket.getStudentId(), STAFF_ROLES);
        return ResponseUtil.success("Support ticket retrieved", ticket);
    }

    @PostMapping("/tickets/{id}/messages")
    public ResponseEntity<CommonResponseDto<SupportMessageDto>> addMessage(
            @PathVariable UUID id,
            @Valid @RequestBody SupportMessageDto dto,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, ALL_ROLES);
        UUID authorId = GatewayAuth.requireUserId(request);
        String authorRole = GatewayAuth.role(request);
        return ResponseUtil.success(
                "Message added",
                supportService.addMessage(id, authorId, authorRole, dto));
    }

    @PutMapping("/tickets/{id}/status")
    public ResponseEntity<CommonResponseDto<SupportTicketDto>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSupportStatusRequest statusRequest,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, STAFF_ROLES);
        UUID staffId = GatewayAuth.requireUserId(request);
        return ResponseUtil.success(
                "Ticket status updated",
                supportService.updateStatus(id, staffId, statusRequest));
    }
}
