package com.yigit.ecommerce.exception;

import org.springframework.http.HttpStatus;

public class BaseRuntimeException extends RuntimeException {

    private final HttpStatus httpStatus;

    public BaseRuntimeException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
