package com.yigit.ecommerce.dto.request.payment;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        String cardNumber,
        String cardHolder,
        String cvv,
        String expireDate
) {}
