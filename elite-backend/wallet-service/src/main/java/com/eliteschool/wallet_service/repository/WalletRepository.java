package com.eliteschool.wallet_service.repository;

import com.eliteschool.wallet_service.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    Optional<Wallet> findByStudentId(UUID studentId);

    List<Wallet> findAllByOrderByBalanceDesc();
}
