package com.eliteschool.reward_service.repository;

import com.eliteschool.reward_service.model.RedeemableItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RedeemableItemRepository extends JpaRepository<RedeemableItem, UUID> {

    // Find an item by name
    Optional<RedeemableItem> findByName(String name);
}
