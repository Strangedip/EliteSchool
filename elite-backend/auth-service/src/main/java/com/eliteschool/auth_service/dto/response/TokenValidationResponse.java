package com.eliteschool.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for token validation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenValidationResponse {
    
    private boolean valid;
    private String message;
}

