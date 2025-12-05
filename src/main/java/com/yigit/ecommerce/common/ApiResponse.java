package com.yigit.ecommerce.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ApiResponse<T> {

    private final boolean success;
    private final int status;
    private final String message;
    private final T data;
    private final Map<String, String> errors;
    private final Instant timestamp;

    private ApiResponse(boolean success, HttpStatus status, String message, T data, Map<String, String> errors) {
        this.success = success;
        this.status = status.value();
        this.message = message;
        this.data = data;
        this.errors = errors;
        this.timestamp = Instant.now();
    }

    /* ---------------------- SUCCESS ---------------------- */

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, HttpStatus.OK, message, data, null);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, HttpStatus.OK, message, null, null);
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return new ApiResponse<>(true, HttpStatus.CREATED, message, data, null);
    }

    /* ---------------------- ERROR ---------------------- */

    public static <T> ApiResponse<T> error(String message, HttpStatus status) {
        return new ApiResponse<>(false, status, message, null, null);
    }

    public static <T> ApiResponse<T> validationError(Map<String, String> errors) {
        return new ApiResponse<>(false, HttpStatus.BAD_REQUEST, "Validation failed", null, errors);
    }

    /* ---------------------- GETTERS ---------------------- */

    public boolean isSuccess() { return success; }

    public int getStatus() { return status; }

    public String getMessage() { return message; }

    public T getData() { return data; }

    public Map<String, String> getErrors() { return errors; }

    public Instant getTimestamp() { return timestamp; }
}
