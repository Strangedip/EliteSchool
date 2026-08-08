package com.eliteschool.wallet_service.exception;

import com.eliteschool.common_utils.exception.AppException;
import org.springframework.http.HttpStatus;

public class TasksNotCompletedException extends AppException {
    public TasksNotCompletedException(String message) {
        super(message, message, "TASKS_NOT_COMPLETED", HttpStatus.BAD_REQUEST);
    }
}
