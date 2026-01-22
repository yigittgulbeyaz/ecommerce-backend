package com.yigit.ecommerce.service;

import com.yigit.ecommerce.dto.request.payment.PaymentRequest;
import com.yigit.ecommerce.model.payment.Payment;

public interface PaymentService {

    Payment pay(Long orderId, PaymentRequest request);
}
