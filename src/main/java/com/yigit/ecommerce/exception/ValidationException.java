package com.yigit.ecommerce.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends BaseRuntimeException {

    public ValidationException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
