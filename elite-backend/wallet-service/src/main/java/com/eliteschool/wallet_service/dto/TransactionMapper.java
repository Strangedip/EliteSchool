package com.eliteschool.wallet_service.dto;

import com.eliteschool.wallet_service.model.Transaction;
import java.util.List;

public final class TransactionMapper {

    private TransactionMapper() {}

    public static TransactionDto toDto(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        return TransactionDto.builder()
                .id(transaction.getId())
                .studentId(transaction.getStudentId())
                .transactionType(transaction.getTransactionType())
                .source(transaction.getSource())
                .points(transaction.getPoints())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    public static List<TransactionDto> toDtoList(List<Transaction> transactions) {
        if (transactions == null) {
            return List.of();
        }
        return transactions.stream().map(TransactionMapper::toDto).toList();
    }
}
