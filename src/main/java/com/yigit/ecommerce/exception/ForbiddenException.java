package com.yigit.ecommerce.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseRuntimeException {

    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
