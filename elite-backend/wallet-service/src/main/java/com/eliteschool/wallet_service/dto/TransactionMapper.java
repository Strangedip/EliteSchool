package com.eliteschool.wallet_service.dto;

import com.eliteschool.wallet_service.model.Transaction;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between Transaction and TransactionDto
 */
public class TransactionMapper {

    /**
     * Convert a Transaction entity to a TransactionDto
     * @param transaction The transaction entity
     * @return The transaction DTO
     */
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
    
    /**
     * Convert a TransactionDto to a Transaction entity
     * @param dto The transaction DTO
     * @return The transaction entity
     */
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
    
    /**
     * Convert a list of Transaction entities to a list of TransactionDtos
     * @param transactions The list of transaction entities
     * @return The list of transaction DTOs
     */
    public static List<TransactionDto> toDtoList(List<Transaction> transactions) {
        if (transactions == null) {
            return List.of();
        }
        
        return transactions.stream()
                .map(TransactionMapper::toDto)
                .collect(Collectors.toList());
    }
} 