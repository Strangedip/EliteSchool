package com.eliteschool.reward_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardWalletDto {
    @NotNull(message = "Student ID is required")
    private UUID studentId;
    
    private int balance;
} 