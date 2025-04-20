package com.eliteschool.common_utils.util;


import java.time.LocalDateTime;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.common_utils.dto.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    // ✅ SUCCESS with data and message
    public static <T> ResponseEntity<CommonResponseDto<T>> success(String message, T data) {
        return ResponseEntity.ok(CommonResponseDto.success(message, data));
    }

    // ✅ SUCCESS with only data
    public static <T> ResponseEntity<CommonResponseDto<T>> success(T data) {
        return ResponseEntity.ok(CommonResponseDto.success("Request successful", data));
    }

    // ❗ ERROR with custom status, message, code
    public static <T> ResponseEntity<CommonResponseDto<T>> error(
            HttpStatus status, String errorCode, String errorDescription, String message) {

        ErrorDetails errorDetails = new ErrorDetails(errorCode, errorDescription, LocalDateTime.now());
        return new ResponseEntity<>(CommonResponseDto.error(message, errorDetails), status);
    }

    // ❗ ERROR with exception object
    public static <T> ResponseEntity<CommonResponseDto<T>> error(
            HttpStatus status, Exception ex, String errorCode) {

        ErrorDetails errorDetails = new ErrorDetails(errorCode, ex.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(CommonResponseDto.error("Request failed", errorDetails), status);
    }

    // ❗ ERROR with default INTERNAL_SERVER_ERROR
    public static <T> ResponseEntity<CommonResponseDto<T>> internalError(Exception ex) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, ex, "INTERNAL_ERROR");
    }
}
