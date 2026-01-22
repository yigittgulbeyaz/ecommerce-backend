package com.yigit.ecommerce.repository;

import com.yigit.ecommerce.model.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
