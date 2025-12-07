package com.yigit.ecommerce.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseRuntimeException {

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
