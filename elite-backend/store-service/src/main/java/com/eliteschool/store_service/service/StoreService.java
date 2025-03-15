package com.eliteschool.store_service.service;

import com.eliteschool.store_service.model.StoreItem;
import com.eliteschool.store_service.repository.StoreItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreItemRepository storeItemRepository;

    /**
     * Get all store items.
     * @return List of available items.
     */
    public List<StoreItem> getAllItems() {
        return storeItemRepository.findAll();
    }

    /**
     * Get item details by ID.
     * @param itemId The ID of the item.
     * @return Optional StoreItem.
     */
    public Optional<StoreItem> getItemById(UUID itemId) {
        return storeItemRepository.findById(itemId);
    }

    /**
     * Add a new store item.
     * @param storeItem The item to add.
     * @return The saved StoreItem.
     */
    public StoreItem addItem(StoreItem storeItem) {
        return storeItemRepository.save(storeItem);
    }

    /**
     * Update an existing store item.
     * @param itemId The ID of the item.
     * @param updatedItem The updated item details.
     * @return Updated StoreItem if found, else empty.
     */
    @Transactional
    public Optional<StoreItem> updateItem(UUID itemId, StoreItem updatedItem) {
        return storeItemRepository.findById(itemId).map(existingItem -> {
            existingItem.setName(updatedItem.getName());
            existingItem.setPrice(updatedItem.getPrice());
            existingItem.setStock(updatedItem.getStock());
            existingItem.setDescription(updatedItem.getDescription());
            return storeItemRepository.save(existingItem);
        });
    }

    /**
     * Delete a store item by ID.
     * @param itemId The ID of the item to delete.
     */
    public void deleteItem(UUID itemId) {
        storeItemRepository.deleteById(itemId);
    }
}
