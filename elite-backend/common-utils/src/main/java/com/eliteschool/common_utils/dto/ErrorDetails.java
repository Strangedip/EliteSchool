package com.eliteschool.common_utils.dto;

import java.time.LocalDateTime;

public class ErrorDetails {
    private String errorCode;
    private String errorDescription;
    private LocalDateTime timestamp;

    public ErrorDetails() {}

    public ErrorDetails(String errorCode, String errorDescription, LocalDateTime timestamp) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
