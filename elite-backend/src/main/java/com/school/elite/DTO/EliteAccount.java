package com.school.elite.DTO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "elite_accounts")
public class EliteAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // start this from 100000
    private Long accountNumber;
    private String accountType;
    private Long accountBal;
    private Integer accountLevel;
    private String username;

    public EliteAccount(String accountType, Long accountBal, Integer accountLevel, String username) {
        this.accountType = accountType;
        this.accountBal = accountBal;
        this.accountLevel = accountLevel;
        this.username = username;
    }
}
