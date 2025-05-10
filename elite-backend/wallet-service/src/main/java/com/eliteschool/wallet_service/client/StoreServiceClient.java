package com.eliteschool.wallet_service.client;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

/**
 * Client interface for communicating with the Store Service.
 * Uses Feign to create a declarative HTTP client.
 */
@FeignClient(name = "store-service")
public interface StoreServiceClient {

    /**
     * Decrements the stock of an item when purchased.
     * 
     * @param itemId The ID of the purchased item.
     * @return ResponseEntity containing the updated item details if successful.
     */
    @PostMapping("/api/store/purchase/{itemId}")
    ResponseEntity<CommonResponseDto<Object>> purchaseItem(@PathVariable("itemId") UUID itemId);
    
    /**
     * Gets the price of an item.
     * 
     * @param itemId The ID of the item.
     * @return ResponseEntity containing the price of the item.
     */
    @GetMapping("/api/store/items/{itemId}/price")
    ResponseEntity<CommonResponseDto<Object>> getItemPrice(@PathVariable("itemId") UUID itemId);
} 