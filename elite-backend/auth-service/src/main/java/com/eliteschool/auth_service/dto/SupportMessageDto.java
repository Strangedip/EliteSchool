package com.eliteschool.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportMessageDto {
    private UUID id;
    private UUID ticketId;
    private UUID authorId;
    private String authorRole;

    @NotBlank(message = "Message body cannot be empty")
    private String body;

    private LocalDateTime createdAt;
}
