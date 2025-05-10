package com.eliteschool.wallet_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "wallets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    private UUID studentId; // Primary key (same as student ID)

    @Column(nullable = false)
    private int balance; // Total points available

}
