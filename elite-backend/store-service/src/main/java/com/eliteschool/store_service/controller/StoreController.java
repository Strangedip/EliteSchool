package com.eliteschool.store_service.controller;

import com.eliteschool.store_service.model.StoreItem;
import com.eliteschool.store_service.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    /**
     * Get all store items.
     * @return List of available items.
     */
    @GetMapping("/items")
    public ResponseEntity<List<StoreItem>> getAllItems() {
        return ResponseEntity.ok(storeService.getAllItems());
    }

    /**
     * Get item details by ID.
     * @param itemId The item's ID.
     * @return ResponseEntity with StoreItem if found.
     */
    @GetMapping("/items/{itemId}")
    public ResponseEntity<StoreItem> getItemById(@PathVariable UUID itemId) {
        return storeService.getItemById(itemId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Add a new store item.
     * @param storeItem The new item.
     * @return ResponseEntity with the added StoreItem.
     */
    @PostMapping("/items")
    public ResponseEntity<StoreItem> addItem(@RequestBody StoreItem storeItem) {
        return ResponseEntity.ok(storeService.addItem(storeItem));
    }

    /**
     * Update an existing store item.
     * @param itemId The item's ID.
     * @param updatedItem Updated item details.
     * @return ResponseEntity with the updated StoreItem.
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<StoreItem> updateItem(@PathVariable UUID itemId, @RequestBody StoreItem updatedItem) {
        return storeService.updateItem(itemId, updatedItem)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Delete a store item by ID.
     * @param itemId The item's ID.
     * @return ResponseEntity with a success message.
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<String> deleteItem(@PathVariable UUID itemId) {
        storeService.deleteItem(itemId);
        return ResponseEntity.ok("Item deleted successfully.");
    }
}
