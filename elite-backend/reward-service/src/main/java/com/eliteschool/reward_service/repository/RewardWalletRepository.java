package com.eliteschool.reward_service.repository;

import com.eliteschool.reward_service.model.RewardWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RewardWalletRepository extends JpaRepository<RewardWallet, UUID> {

    // Find wallet by student ID
    Optional<RewardWallet> findByStudentId(UUID studentId);
}
