package com.eliteschool.store_service.repository;

import com.eliteschool.store_service.model.StorePurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StorePurchaseRepository extends JpaRepository<StorePurchase, UUID> {
    List<StorePurchase> findByStudentIdOrderByClaimedAtDesc(UUID studentId);

    List<StorePurchase> findByItemIdOrderByClaimedAtDesc(UUID itemId);

    List<StorePurchase> findAllByOrderByClaimedAtDesc();
}
