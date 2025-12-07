package com.yigit.ecommerce.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseRuntimeException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
