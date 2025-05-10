package com.eliteschool.wallet_service.exception;

import com.eliteschool.common_utils.exception.AppException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an item is out of stock.
 */
public class ItemOutOfStockException extends AppException {
    
    public ItemOutOfStockException(String message) {
        super(message, "This item is currently out of stock", "ITEM_OUT_OF_STOCK", HttpStatus.OK);
    }
    
    public ItemOutOfStockException(String message, Throwable cause) {
        super(message, "This item is currently out of stock", "ITEM_OUT_OF_STOCK", HttpStatus.OK);
    }
} 