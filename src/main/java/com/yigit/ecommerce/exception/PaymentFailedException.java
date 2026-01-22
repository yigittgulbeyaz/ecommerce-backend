package com.yigit.ecommerce.exception;

import org.springframework.http.HttpStatus;

public class PaymentFailedException extends BaseRuntimeException {

    public PaymentFailedException(String message) {
        super(message, HttpStatus.PAYMENT_REQUIRED);
    }
}
