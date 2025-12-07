package com.yigit.ecommerce.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends BaseRuntimeException {
    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
