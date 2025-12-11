package com.eliteschool.wallet_service.exception;

import com.eliteschool.common_utils.exception.AppException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a student doesn't have enough points for a redemption.
 */
public class InsufficientPointsException extends AppException {
    
    public InsufficientPointsException(String message) {
        super(message, "You don't have enough points for this transaction", "INSUFFICIENT_POINTS", HttpStatus.BAD_REQUEST);
    }
    
    public InsufficientPointsException(int required, int available) {
        super(
            String.format("Insufficient points: required %d, available %d", required, available),
            "You don't have enough points for this transaction",
            "INSUFFICIENT_POINTS",
            HttpStatus.BAD_REQUEST
        );
    }
} 