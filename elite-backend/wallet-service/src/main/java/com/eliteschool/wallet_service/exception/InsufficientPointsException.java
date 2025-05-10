package com.eliteschool.wallet_service.exception;

import com.eliteschool.common_utils.exception.AppException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a student doesn't have enough points for a redemption.
 */
public class InsufficientPointsException extends AppException {
    
    public InsufficientPointsException(String message) {
        super(message, "You don't have enough points for this item", "INSUFFICIENT_POINTS", HttpStatus.OK);
    }
    
    public InsufficientPointsException(String message, Throwable cause) {
        super(message, "You don't have enough points for this item", "INSUFFICIENT_POINTS", HttpStatus.OK);
    }
} 