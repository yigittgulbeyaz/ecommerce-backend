package com.yigit.ecommerce.model.payment;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private String transactionId;

    @Enumerated(EnumType.STRING)
    private PaymentProvider provider;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private BigDecimal amount;

    private LocalDateTime createdAt;

    protected Payment() {
        // JPA
    }

    /* ================= FACTORY METHOD ================= */

    public static Payment create(
            Long orderId,
            PaymentProvider provider,
            BigDecimal amount,
            boolean success
    ) {
        Payment payment = new Payment();
        payment.orderId = orderId;
        payment.provider = provider;
        payment.amount = amount;
        payment.transactionId = UUID.randomUUID().toString();
        payment.status = success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
        payment.createdAt = LocalDateTime.now();
        return payment;
    }

    /* ================= DOMAIN HELPERS ================= */

    public boolean isSuccessful() {
        return status == PaymentStatus.SUCCESS;
    }

    public boolean isFailed() {
        return status == PaymentStatus.FAILED;
    }

    /* ================= GETTERS / SETTERS ================= */

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
