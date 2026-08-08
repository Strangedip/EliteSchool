package com.eliteschool.wallet_service.dto;

import com.eliteschool.wallet_service.model.Transaction;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionMapper {

    public static TransactionDto toDto(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        
        return TransactionDto.builder()
                .id(transaction.getId())
                .studentId(transaction.getStudentId())
                .transactionType(transaction.getTransactionType())
                .points(transaction.getPoints())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
    
    public static Transaction toEntity(TransactionDto dto) {
        if (dto == null) {
            return null;
        }
        
        return Transaction.builder()
                .id(dto.getId())
                .studentId(dto.getStudentId())
                .transactionType(dto.getTransactionType())
                .points(dto.getPoints())
                .description(dto.getDescription())
                .build();
    }
    
    public static List<TransactionDto> toDtoList(List<Transaction> transactions) {
        if (transactions == null) {
            return List.of();
        }
        
        return transactions.stream()
                .map(TransactionMapper::toDto)
                .collect(Collectors.toList());
    }
}
