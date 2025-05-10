package com.eliteschool.store_service.service;

import com.eliteschool.store_service.dto.StoreItemDto;
import com.eliteschool.store_service.dto.StoreItemMapper;
import com.eliteschool.store_service.model.StoreItem;
import com.eliteschool.store_service.repository.StoreItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService {

    private final StoreItemRepository storeItemRepository;

    /**
     * Get all store items.
     * @return List of available items.
     */
    public List<StoreItemDto> getAllItems() {
        return StoreItemMapper.toDtoList(storeItemRepository.findAll());
    }

    /**
     * Get item details by ID.
     * @param itemId The ID of the item.
     * @return Optional StoreItemDto.
     */
    public Optional<StoreItemDto> getItemById(UUID itemId) {
        return storeItemRepository.findById(itemId)
                .map(StoreItemMapper::toDto);
    }

    /**
     * Add a new store item.
     * @param itemDto The item to add.
     * @return The saved StoreItemDto.
     */
    public StoreItemDto addItem(StoreItemDto itemDto) {
        StoreItem storeItem = StoreItemMapper.toEntity(itemDto);
        return StoreItemMapper.toDto(storeItemRepository.save(storeItem));
    }

    /**
     * Update an existing store item.
     * @param itemId The ID of the item.
     * @param updatedItemDto The updated item details.
     * @return Updated StoreItemDto if found, else empty.
     */
    @Transactional
    public Optional<StoreItemDto> updateItem(UUID itemId, StoreItemDto updatedItemDto) {
        return storeItemRepository.findById(itemId).map(existingItem -> {
            existingItem.setName(updatedItemDto.getName());
            existingItem.setPrice(updatedItemDto.getPrice());
            existingItem.setStock(updatedItemDto.getStock());
            existingItem.setDescription(updatedItemDto.getDescription());
            existingItem.setImageUrl(updatedItemDto.getImageUrl());
            return StoreItemMapper.toDto(storeItemRepository.save(existingItem));
        });
    }

    /**
     * Delete a store item by ID.
     * @param itemId The ID of the item to delete.
     */
    public void deleteItem(UUID itemId) {
        storeItemRepository.deleteById(itemId);
        log.info("Item with ID {} deleted successfully", itemId);
    }
    
    /**
     * Decrement the stock of an item when purchased.
     * @param itemId The ID of the purchased item.
     * @return Updated StoreItemDto if found and has stock, else empty.
     */
    @Transactional
    public Optional<StoreItemDto> decrementStock(UUID itemId) {
        return storeItemRepository.findById(itemId).flatMap(item -> {
            if (item.getStock() <= 0) {
                log.warn("Item with ID {} is out of stock", itemId);
                return Optional.empty();
            }
            
            item.setStock(item.getStock() - 1);
            log.info("Item with ID {} stock decremented to {}", itemId, item.getStock());
            return Optional.of(StoreItemMapper.toDto(storeItemRepository.save(item)));
        });
    }
}
