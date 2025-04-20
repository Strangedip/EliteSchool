package com.eliteschool.common_utils.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException {
    private final String errorCode;
    private final String friendlyMessage;
    private final HttpStatus status;

    public AppException(String message, String friendlyMessage, String errorCode, HttpStatus status) {
        super(message);
        this.friendlyMessage = friendlyMessage;
        this.errorCode = errorCode;
        this.status = status;
    }

}
