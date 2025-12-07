package com.yigit.ecommerce.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends BaseRuntimeException {

    public InvalidTokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
