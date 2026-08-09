package com.eliteschool.wallet_service.repository;

import com.eliteschool.wallet_service.model.Transaction;
import com.eliteschool.wallet_service.model.enums.TransactionSource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByStudentIdOrderByCreatedAtDesc(UUID studentId);

    List<Transaction> findBySourceOrderByCreatedAtDesc(TransactionSource source, Pageable pageable);

    boolean existsByReferenceId(String referenceId);
}
