package com.eliteschool.wallet_service.exception;

import com.eliteschool.common_utils.exception.AppException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an item is out of stock.
 */
public class ItemOutOfStockException extends AppException {
    
    public ItemOutOfStockException(String message) {
        super(message, "This item is currently out of stock", "ITEM_OUT_OF_STOCK", HttpStatus.BAD_REQUEST);
    }
    
    public ItemOutOfStockException(String itemName) {
        super(
            String.format("Item '%s' is out of stock", itemName),
            "This item is currently out of stock",
            "ITEM_OUT_OF_STOCK",
            HttpStatus.BAD_REQUEST
        );
    }
} 