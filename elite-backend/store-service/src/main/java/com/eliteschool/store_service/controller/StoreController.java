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

    @GetMapping("/items")
    public ResponseEntity<CommonResponseDto<List<StoreItemDto>>> getAllItems() {
        return ResponseUtil.success("Items retrieved", storeService.getAllItems());
    }

    @GetMapping("/items/{itemId}")
    public ResponseEntity<CommonResponseDto<StoreItemDto>> getItemById(@PathVariable UUID itemId) {
        return storeService.getItemById(itemId)
                .map(item -> ResponseUtil.success("Item retrieved", item))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "ITEM_NOT_FOUND",
                        "Item not found: " + itemId, null));
    }

    @PostMapping("/items")
    public ResponseEntity<CommonResponseDto<StoreItemDto>> addItem(@Valid @RequestBody StoreItemDto storeItemDto) {
        log.info("Adding item: {}", storeItemDto.getName());
        return ResponseUtil.success("Item added", storeService.addItem(storeItemDto));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CommonResponseDto<StoreItemDto>> updateItem(
            @PathVariable UUID itemId,
            @Valid @RequestBody StoreItemDto updatedItemDto) {
        return storeService.updateItem(itemId, updatedItemDto)
                .map(item -> ResponseUtil.success("Item updated", item))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "ITEM_NOT_FOUND",
                        "Item not found: " + itemId, null));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CommonResponseDto<Void>> deleteItem(@PathVariable UUID itemId) {
        try {
            storeService.deleteItem(itemId);
            return ResponseUtil.success("Item deleted", null);
        } catch (Exception e) {
            log.error("Error deleting item {}: {}", itemId, e.getMessage());
            return ResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, "DELETE_ERROR", e.getMessage(), null);
        }
    }

    // Called by wallet-service via Feign to decrement stock on purchase
    @PostMapping("/purchase/{itemId}")
    public ResponseEntity<CommonResponseDto<StoreItemDto>> purchaseItem(@PathVariable UUID itemId) {
        return storeService.decrementStock(itemId)
                .map(item -> ResponseUtil.success("Purchase successful", item))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.BAD_REQUEST, "PURCHASE_FAILED",
                        "Item out of stock or not found", null));
    }

    @GetMapping("/items/{itemId}/price")
    public ResponseEntity<CommonResponseDto<Integer>> getItemPrice(@PathVariable UUID itemId) {
        return storeService.getItemById(itemId)
                .map(item -> ResponseUtil.success("Price retrieved", item.getPrice()))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "ITEM_NOT_FOUND",
                        "Item not found: " + itemId, null));
    }
}
