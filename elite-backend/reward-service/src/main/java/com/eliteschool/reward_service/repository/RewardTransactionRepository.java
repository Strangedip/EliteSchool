package com.eliteschool.reward_service.repository;

import com.eliteschool.reward_service.model.RewardTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RewardTransactionRepository extends JpaRepository<RewardTransaction, UUID> {

    // Find all transactions by student ID (ordered by latest first)
    List<RewardTransaction> findByStudentIdOrderByCreatedAtDesc(UUID studentId);
}
