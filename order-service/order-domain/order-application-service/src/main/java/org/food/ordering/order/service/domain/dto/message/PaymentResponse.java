package org.food.ordering.order.service.domain.dto.message;

import org.food.ordering.domain.valueobject.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class PaymentResponse {
    private String id;
    private String sagaId;
    private String orderId;
    private String paymentId;
    private String customerId;
    private BigDecimal price;
    private Instant createdAt;
    private PaymentStatus paymentStatus;
    private List<String> failureMessages;

    public PaymentResponse(String id, String sagaId, String orderId, String paymentId, String customerId, BigDecimal price, Instant createdAt, PaymentStatus paymentStatus, List<String> failureMessages) {
        this.id = id;
        this.sagaId = sagaId;
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.customerId = customerId;
        this.price = price;
        this.createdAt = createdAt;
        this.paymentStatus = paymentStatus;
        this.failureMessages = failureMessages;
    }

    public String getId() {
        return id;
    }

    public String getSagaId() {
        return sagaId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }
}
