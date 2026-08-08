package com.eliteschool.store_service.repository;

import com.eliteschool.store_service.model.StoreItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StoreItemRepository extends JpaRepository<StoreItem, UUID> {

    Optional<StoreItem> findByName(String name);
}
