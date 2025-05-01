package com.eliteschool.reward_service.dto;

import com.eliteschool.reward_service.model.enums.TransactionType;
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
public class RewardTransactionDto {
    private UUID id;
    
    @NotNull(message = "Student ID is required")
    private UUID studentId;
    
    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;
    
    @NotNull(message = "Points value is required")
    @Min(value = 1, message = "Points must be at least 1")
    private Integer points;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private LocalDateTime createdAt;
} 