package com.eliteschool.wallet_service.dto;

import com.eliteschool.wallet_service.model.enums.NominationStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NominationDto {
    private UUID id;

    @NotNull(message = "Student id is required")
    private UUID studentId;

    private UUID nominatedBy;
    private String nominatorRole;

    @NotNull(message = "Suggested points are required")
    @Min(value = 1, message = "Suggested points must be at least 1")
    private Integer suggestedPoints;

    @NotBlank(message = "Reason is required")
    private String reason;

    private String evidenceNote;

    private NominationStatus status;
    private UUID reviewedBy;
    private LocalDateTime reviewedAt;
    private String reviewNotes;
    private String walletReferenceId;
    private Integer awardedPoints;
    private LocalDateTime createdAt;
}
