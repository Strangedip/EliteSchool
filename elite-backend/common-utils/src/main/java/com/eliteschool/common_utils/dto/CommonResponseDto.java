package com.eliteschool.common_utils.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponseDto<T> {

    private boolean success;
    private String message;
    private T data;
    private ErrorDetails error;

    public CommonResponseDto() {}

    public CommonResponseDto(boolean success, String message, T data, ErrorDetails error) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.error = error;
    }

    public static <T> CommonResponseDto<T> success(String message, T data) {
        return new CommonResponseDto<>(true, message, data, null);
    }

    public static <T> CommonResponseDto<T> error(String message, ErrorDetails error) {
        return new CommonResponseDto<>(false, message, null, error);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ErrorDetails getError() {
        return error;
    }

    public void setError(ErrorDetails error) {
        this.error = error;
    }
}

