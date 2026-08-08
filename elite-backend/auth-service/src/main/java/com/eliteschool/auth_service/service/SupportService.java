package com.eliteschool.auth_service.service;

import com.eliteschool.auth_service.dto.SupportMessageDto;
import com.eliteschool.auth_service.dto.SupportTicketDto;
import com.eliteschool.auth_service.dto.request.UpdateSupportStatusRequest;
import com.eliteschool.auth_service.mapper.SupportMapper;
import com.eliteschool.auth_service.model.SupportMessage;
import com.eliteschool.auth_service.model.SupportTicket;
import com.eliteschool.auth_service.model.User;
import com.eliteschool.auth_service.model.enums.SupportTicketStatus;
import com.eliteschool.auth_service.repository.SupportMessageRepository;
import com.eliteschool.auth_service.repository.SupportTicketRepository;
import com.eliteschool.auth_service.repository.UserRepository;
import com.eliteschool.common_utils.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupportService {

    private static final Set<SupportTicketStatus> STAFF_UPDATABLE_STATUSES =
            EnumSet.of(SupportTicketStatus.IN_PROGRESS, SupportTicketStatus.RESOLVED, SupportTicketStatus.CLOSED);

    private static final Set<SupportTicketStatus> STUDENT_REPLYABLE_STATUSES =
            EnumSet.of(SupportTicketStatus.OPEN, SupportTicketStatus.IN_PROGRESS);

    private final SupportTicketRepository ticketRepository;
    private final SupportMessageRepository messageRepository;
    private final UserRepository userRepository;

    public SupportTicketDto createTicket(UUID studentId, SupportTicketDto dto) {
        SupportTicket ticket = SupportTicket.builder()
                .studentId(studentId)
                .subject(dto.getSubject().trim())
                .body(dto.getBody().trim())
                .category(dto.getCategory())
                .status(SupportTicketStatus.OPEN)
                .build();

        SupportTicket saved = ticketRepository.save(ticket);
        log.info("Support ticket {} created by student {}", saved.getId(), studentId);
        return enrich(SupportMapper.toDto(saved, List.of()));
    }

    public List<SupportTicketDto> getMyTickets(UUID studentId) {
        return enrichAll(SupportMapper.toDtoList(ticketRepository.findByStudentIdOrderByCreatedAtDesc(studentId)));
    }

    public List<SupportTicketDto> getAllTickets(SupportTicketStatus status) {
        List<SupportTicket> tickets = status == null
                ? ticketRepository.findAllByOrderByCreatedAtDesc()
                : ticketRepository.findByStatusOrderByCreatedAtDesc(status);
        return enrichAll(SupportMapper.toDtoList(tickets));
    }

    public SupportTicketDto getTicket(UUID ticketId) {
        SupportTicket ticket = findTicketOrThrow(ticketId);
        List<SupportMessage> messages = messageRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
        return enrich(SupportMapper.toDto(ticket, messages));
    }

    @Transactional
    public SupportMessageDto addMessage(UUID ticketId, UUID authorId, String authorRole, SupportMessageDto dto) {
        SupportTicket ticket = findTicketOrThrow(ticketId);
        boolean isStaff = isStaffRole(authorRole);
        boolean isOwner = ticket.getStudentId().equals(authorId);

        if (!isStaff && !isOwner) {
            throw forbidden("You can only reply to your own support tickets.");
        }
        if (!isStaff && !STUDENT_REPLYABLE_STATUSES.contains(ticket.getStatus())) {
            throw forbidden("You can only reply to open or in-progress tickets.");
        }

        SupportMessage message = SupportMessage.builder()
                .ticketId(ticketId)
                .authorId(authorId)
                .authorRole(authorRole)
                .body(dto.getBody().trim())
                .build();

        SupportMessage saved = messageRepository.save(message);

        if (isStaff && ticket.getStatus() == SupportTicketStatus.OPEN) {
            ticket.setStatus(SupportTicketStatus.IN_PROGRESS);
            ticketRepository.save(ticket);
        }

        log.info("Support message added to ticket {} by {}", ticketId, authorId);
        return SupportMapper.toMessageDto(saved);
    }

    @Transactional
    public SupportTicketDto updateStatus(UUID ticketId, UUID staffId, UpdateSupportStatusRequest request) {
        if (!STAFF_UPDATABLE_STATUSES.contains(request.getStatus())) {
            throw new AppException(
                    "Invalid status",
                    "Staff may only set status to IN_PROGRESS, RESOLVED, or CLOSED.",
                    "INVALID_STATUS",
                    HttpStatus.BAD_REQUEST);
        }

        SupportTicket ticket = findTicketOrThrow(ticketId);
        ticket.setStatus(request.getStatus());

        if (request.getResolutionNotes() != null && !request.getResolutionNotes().isBlank()) {
            ticket.setResolutionNotes(request.getResolutionNotes().trim());
        }

        if (request.getStatus() == SupportTicketStatus.RESOLVED
                || request.getStatus() == SupportTicketStatus.CLOSED) {
            ticket.setResolvedBy(staffId);
        }

        SupportTicket saved = ticketRepository.save(ticket);
        List<SupportMessage> messages = messageRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
        log.info("Support ticket {} status set to {} by {}", ticketId, request.getStatus(), staffId);
        return enrich(SupportMapper.toDto(saved, messages));
    }

    private List<SupportTicketDto> enrichAll(List<SupportTicketDto> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            return tickets == null ? List.of() : tickets;
        }

        Set<UUID> ids = tickets.stream()
                .map(SupportTicketDto::getStudentId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Map<UUID, String> names = new HashMap<>();
        if (!ids.isEmpty()) {
            for (User user : userRepository.findAllById(ids)) {
                String label = user.getName() != null && !user.getName().isBlank()
                        ? user.getName()
                        : user.getUsername();
                names.put(user.getEliteId(), label);
            }
        }

        for (SupportTicketDto dto : tickets) {
            if (dto.getStudentId() != null) {
                dto.setStudentName(names.get(dto.getStudentId()));
            }
        }
        return tickets;
    }

    private SupportTicketDto enrich(SupportTicketDto dto) {
        if (dto == null || dto.getStudentId() == null) {
            return dto;
        }
        userRepository.findById(dto.getStudentId()).ifPresent(user -> {
            String label = user.getName() != null && !user.getName().isBlank()
                    ? user.getName()
                    : user.getUsername();
            dto.setStudentName(label);
        });
        return dto;
    }

    private SupportTicket findTicketOrThrow(UUID ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new AppException(
                        "Ticket not found",
                        "Support ticket not found: " + ticketId,
                        "TICKET_NOT_FOUND",
                        HttpStatus.NOT_FOUND));
    }

    private static boolean isStaffRole(String role) {
        if (role == null) {
            return false;
        }
        String normalized = role.trim().toUpperCase();
        return "FACULTY".equals(normalized)
                || "ADMIN".equals(normalized)
                || "MANAGEMENT".equals(normalized);
    }

    private static AppException forbidden(String message) {
        return new AppException(message, message, "FORBIDDEN", HttpStatus.FORBIDDEN);
    }
}
