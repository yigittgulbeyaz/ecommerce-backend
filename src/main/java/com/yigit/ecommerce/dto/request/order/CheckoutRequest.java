package com.yigit.ecommerce.dto.request.order;

import com.yigit.ecommerce.dto.request.payment.PaymentRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
        @NotNull(message = "Address ID is required")
        Long addressId,

        @Valid
        @NotNull(message = "Payment details are required")
        PaymentRequest paymentRequest
) {}