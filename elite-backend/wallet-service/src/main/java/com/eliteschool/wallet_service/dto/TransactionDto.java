package com.eliteschool.wallet_service.dto;

import com.eliteschool.wallet_service.model.enums.TransactionSource;
import com.eliteschool.wallet_service.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private UUID id;
    private UUID studentId;
    private String studentName;
    private TransactionType transactionType;
    private TransactionSource source;
    private int points;
    private String description;
    private LocalDateTime createdAt;
}
