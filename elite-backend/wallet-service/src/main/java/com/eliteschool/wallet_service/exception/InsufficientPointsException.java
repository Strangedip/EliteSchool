package com.eliteschool.wallet_service.exception;

import com.eliteschool.common_utils.exception.AppException;
import org.springframework.http.HttpStatus;

public class InsufficientPointsException extends AppException {

    public InsufficientPointsException(String message) {
        super(message, "You don't have enough points for this transaction", "INSUFFICIENT_POINTS", HttpStatus.CONFLICT);
    }
}
