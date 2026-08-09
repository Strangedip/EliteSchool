package com.eliteschool.wallet_service.model;

import com.eliteschool.wallet_service.model.enums.TransactionSource;
import com.eliteschool.wallet_service.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_transactions_source_created", columnList = "source, createdAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID studentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    @Builder.Default
    private TransactionSource source = TransactionSource.OTHER;

    @Column(nullable = false)
    private int points;

    @Column(nullable = false)
    private String description;

    /** Optional unique key for idempotent credits (e.g. task submission id). */
    @Column(unique = true)
    private String referenceId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.source == null) {
            this.source = TransactionSource.OTHER;
        }
    }
}
