package com.eliteschool.reward_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "reward_wallets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardWallet {

    @Id
    private UUID studentId; // Primary key (same as student ID)

    @Column(nullable = false)
    private int balance; // Total reward points available

}
