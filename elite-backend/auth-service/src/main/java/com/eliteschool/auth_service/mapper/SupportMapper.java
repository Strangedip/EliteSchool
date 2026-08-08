package com.eliteschool.auth_service.mapper;

import com.eliteschool.auth_service.dto.SupportMessageDto;
import com.eliteschool.auth_service.dto.SupportTicketDto;
import com.eliteschool.auth_service.model.SupportMessage;
import com.eliteschool.auth_service.model.SupportTicket;

import java.util.List;
import java.util.stream.Collectors;

public final class SupportMapper {

    private SupportMapper() {}

    public static SupportTicketDto toDto(SupportTicket ticket) {
        if (ticket == null) {
            return null;
        }

        return SupportTicketDto.builder()
                .id(ticket.getId())
                .studentId(ticket.getStudentId())
                .subject(ticket.getSubject())
                .body(ticket.getBody())
                .category(ticket.getCategory())
                .status(ticket.getStatus())
                .resolutionNotes(ticket.getResolutionNotes())
                .resolvedBy(ticket.getResolvedBy())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }

    public static SupportTicketDto toDto(SupportTicket ticket, List<SupportMessage> messages) {
        SupportTicketDto dto = toDto(ticket);
        if (dto != null) {
            dto.setMessages(toMessageDtoList(messages));
        }
        return dto;
    }

    public static SupportTicket toEntity(SupportTicketDto dto) {
        if (dto == null) {
            return null;
        }

        return SupportTicket.builder()
                .studentId(dto.getStudentId())
                .subject(dto.getSubject())
                .body(dto.getBody())
                .category(dto.getCategory())
                .status(dto.getStatus())
                .resolutionNotes(dto.getResolutionNotes())
                .resolvedBy(dto.getResolvedBy())
                .build();
    }

    public static SupportMessageDto toMessageDto(SupportMessage message) {
        if (message == null) {
            return null;
        }

        return SupportMessageDto.builder()
                .id(message.getId())
                .ticketId(message.getTicketId())
                .authorId(message.getAuthorId())
                .authorRole(message.getAuthorRole())
                .body(message.getBody())
                .createdAt(message.getCreatedAt())
                .build();
    }

    public static List<SupportTicketDto> toDtoList(List<SupportTicket> tickets) {
        if (tickets == null) {
            return List.of();
        }
        return tickets.stream()
                .map(SupportMapper::toDto)
                .collect(Collectors.toList());
    }

    public static List<SupportMessageDto> toMessageDtoList(List<SupportMessage> messages) {
        if (messages == null) {
            return List.of();
        }
        return messages.stream()
                .map(SupportMapper::toMessageDto)
                .collect(Collectors.toList());
    }
}
