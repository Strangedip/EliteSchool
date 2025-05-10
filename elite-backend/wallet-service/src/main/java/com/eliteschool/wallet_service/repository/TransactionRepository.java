package com.eliteschool.wallet_service.repository;

import com.eliteschool.wallet_service.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    // Find all transactions by student ID (ordered by latest first)
    List<Transaction> findByStudentIdOrderByCreatedAtDesc(UUID studentId);
}
