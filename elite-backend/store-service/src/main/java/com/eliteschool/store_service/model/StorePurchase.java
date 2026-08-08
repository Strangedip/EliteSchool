package com.eliteschool.store_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "store_purchases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorePurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID studentId;

    @Column(nullable = false)
    private UUID itemId;

    private String itemName;

    private String studentName;

    @Column(nullable = false)
    private LocalDateTime claimedAt;

    @PrePersist
    void onCreate() {
        if (claimedAt == null) {
            claimedAt = LocalDateTime.now();
        }
    }
}
