package com.eliteschool.reward_service.dto;

import com.eliteschool.reward_service.model.RewardTransaction;
import java.util.List;
import java.util.stream.Collectors;

public class RewardTransactionMapper {

    public static RewardTransactionDto toDto(RewardTransaction transaction) {
        if (transaction == null) {
            return null;
        }
        
        return RewardTransactionDto.builder()
                .id(transaction.getId())
                .studentId(transaction.getStudentId())
                .transactionType(transaction.getTransactionType())
                .points(transaction.getPoints())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
    
    public static RewardTransaction toEntity(RewardTransactionDto dto) {
        if (dto == null) {
            return null;
        }
        
        return RewardTransaction.builder()
                .id(dto.getId())
                .studentId(dto.getStudentId())
                .transactionType(dto.getTransactionType())
                .points(dto.getPoints())
                .description(dto.getDescription())
                .createdAt(dto.getCreatedAt())
                .build();
    }
    
    public static List<RewardTransactionDto> toDtoList(List<RewardTransaction> transactions) {
        if (transactions == null) {
            return List.of();
        }
        
        return transactions.stream()
                .map(RewardTransactionMapper::toDto)
                .collect(Collectors.toList());
    }
} 