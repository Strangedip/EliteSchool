package com.school.elite.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_accounts")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // start this from 100000
    private Long accountNumber;
    private String accountType;
    private Long accountBal;
    private Integer accountLevel;
    private String username;

    public UserAccount(String accountType, Long accountBal, Integer accountLevel, String username) {
        this.accountType = accountType;
        this.accountBal = accountBal;
        this.accountLevel = accountLevel;
        this.username = username;
    }
}
