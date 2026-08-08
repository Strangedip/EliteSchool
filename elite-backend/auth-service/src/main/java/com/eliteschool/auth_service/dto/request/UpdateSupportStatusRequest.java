package com.eliteschool.auth_service.dto.request;

import com.eliteschool.auth_service.model.enums.SupportTicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSupportStatusRequest {

    @NotNull(message = "Status is required")
    private SupportTicketStatus status;

    private String resolutionNotes;
}
