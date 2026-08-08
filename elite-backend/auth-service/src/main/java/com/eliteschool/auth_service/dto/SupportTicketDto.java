package com.eliteschool.auth_service.dto;

import com.eliteschool.auth_service.model.enums.SupportCategory;
import com.eliteschool.auth_service.model.enums.SupportTicketStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportTicketDto {
    private UUID id;
    private UUID studentId;
    /** Display name for staff UIs; not persisted. */
    private String studentName;

    @NotBlank(message = "Subject cannot be empty")
    private String subject;

    @NotBlank(message = "Body cannot be empty")
    private String body;

    @NotNull(message = "Category is required")
    private SupportCategory category;

    private SupportTicketStatus status;
    private String resolutionNotes;
    private UUID resolvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private List<SupportMessageDto> messages = new ArrayList<>();
}
