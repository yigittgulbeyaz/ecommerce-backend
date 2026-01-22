package com.yigit.ecommerce.service.impl;

import com.yigit.ecommerce.dto.request.payment.PaymentRequest;
import com.yigit.ecommerce.model.payment.Payment;
import com.yigit.ecommerce.model.payment.PaymentProvider;
import com.yigit.ecommerce.repository.PaymentRepository;
import com.yigit.ecommerce.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class MockPaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public MockPaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment pay(Long orderId, PaymentRequest request) {

        boolean success = isPaymentSuccessful(request);

        Payment payment = Payment.create(
                orderId,
                PaymentProvider.MOCK,
                request.amount(),
                success
        );

        return paymentRepository.save(payment);
    }

    /* ================= PRIVATE ================= */

    private boolean isPaymentSuccessful(PaymentRequest request) {
        // mock rule: card number ending with 0 = success
        return request.cardNumber() != null
                && request.cardNumber().endsWith("0");
    }
}
