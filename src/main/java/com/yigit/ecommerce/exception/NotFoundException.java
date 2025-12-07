package com.yigit.ecommerce.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseRuntimeException {

    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
