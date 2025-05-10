package com.eliteschool.wallet_service.model;

import com.eliteschool.wallet_service.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
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
    private UUID studentId; // Student who earned/spent points

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType; // CREDIT or DEBIT

    @Column(nullable = false)
    private int points; // Points added or deducted

    @Column(nullable = false)
    private String description; // Description (e.g., "Task Completed" or "Purchased Item")

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
