package com.eliteschool.store_service.controller;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.common_utils.util.ResponseUtil;
import com.eliteschool.store_service.dto.StoreItemDto;
import com.eliteschool.store_service.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
@Slf4j
public class StoreController {

    private final StoreService storeService;

    /**
     * Get all store items.
     * @return List of available items.
     */
    @GetMapping("/items")
    public ResponseEntity<CommonResponseDto<List<StoreItemDto>>> getAllItems() {
        log.info("Request received to get all store items");
        return ResponseUtil.success("Items retrieved successfully", storeService.getAllItems());
    }

    /**
     * Get item details by ID.
     * @param itemId The item's ID.
     * @return ResponseEntity with StoreItemDto if found.
     */
    @GetMapping("/items/{itemId}")
    public ResponseEntity<CommonResponseDto<StoreItemDto>> getItemById(@PathVariable UUID itemId) {
        log.info("Request received to get item with ID: {}", itemId);
        return storeService.getItemById(itemId)
                .map(item -> ResponseUtil.success("Item retrieved successfully", item))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "ITEM_NOT_FOUND", 
                          "Item with ID " + itemId + " not found", "Item not found"));
    }

    /**
     * Add a new store item.
     * @param storeItemDto The new item.
     * @return ResponseEntity with the added StoreItemDto.
     */
    @PostMapping("/items")
    public ResponseEntity<CommonResponseDto<StoreItemDto>> addItem(@Valid @RequestBody StoreItemDto storeItemDto) {
        log.info("Request received to add new item: {}", storeItemDto.getName());
        return ResponseUtil.success("Item added successfully", storeService.addItem(storeItemDto));
    }

    /**
     * Update an existing store item.
     * @param itemId The item's ID.
     * @param updatedItemDto Updated item details.
     * @return ResponseEntity with the updated StoreItemDto.
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CommonResponseDto<StoreItemDto>> updateItem(
            @PathVariable UUID itemId, 
            @Valid @RequestBody StoreItemDto updatedItemDto) {
        log.info("Request received to update item with ID: {}", itemId);
        return storeService.updateItem(itemId, updatedItemDto)
                .map(item -> ResponseUtil.success("Item updated successfully", item))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "ITEM_NOT_FOUND", 
                          "Item with ID " + itemId + " not found", "Item not found"));
    }

    /**
     * Delete a store item by ID.
     * @param itemId The item's ID.
     * @return ResponseEntity with a success message.
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CommonResponseDto<Void>> deleteItem(@PathVariable UUID itemId) {
        log.info("Request received to delete item with ID: {}", itemId);
        try {
            storeService.deleteItem(itemId);
            return ResponseUtil.success("Item deleted successfully", null);
        } catch (Exception e) {
            log.error("Error deleting item with ID: {}", itemId, e);
            return ResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, "DELETE_ERROR", 
                    e.getMessage(), "Error deleting item");
        }
    }
    
    /**
     * Process item purchase/redemption by a student.
     * This endpoint is meant to be called by the reward service.
     * @param itemId The ID of the item being redeemed.
     * @return ResponseEntity with the updated item after purchase.
     */
    @PostMapping("/purchase/{itemId}")
    public ResponseEntity<CommonResponseDto<StoreItemDto>> purchaseItem(@PathVariable UUID itemId) {
        log.info("Request received to purchase item with ID: {}", itemId);
        return storeService.decrementStock(itemId)
                .map(item -> ResponseUtil.success("Item purchase successful", item))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.BAD_REQUEST, "PURCHASE_FAILED", 
                          "Item out of stock or not found", "Purchase failed"));
    }
}
