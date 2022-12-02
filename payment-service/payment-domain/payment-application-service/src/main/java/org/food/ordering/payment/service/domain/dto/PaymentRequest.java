package org.food.ordering.payment.service.domain.dto;

import org.food.ordering.domain.valueobject.PaymentOrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentRequest {
    private String id;
    private String sagaId;
    private String orderId;
    private String customerId;
    private BigDecimal price;
    private Instant createdAt;
    private PaymentOrderStatus paymentOrderStatus;

    public PaymentRequest(String id, String sagaId, String orderId, String customerId, BigDecimal price, Instant createdAt, PaymentOrderStatus paymentOrderStatus) {
        this.id = id;
        this.sagaId = sagaId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.price = price;
        this.createdAt = createdAt;
        this.paymentOrderStatus = paymentOrderStatus;
    }

    private PaymentRequest(Builder builder) {
        id = builder.id;
        sagaId = builder.sagaId;
        orderId = builder.orderId;
        customerId = builder.customerId;
        price = builder.price;
        createdAt = builder.createdAt;
        paymentOrderStatus = builder.paymentOrderStatus;
    }

    public void setPaymentOrderStatus(PaymentOrderStatus paymentOrderStatus) {
        this.paymentOrderStatus = paymentOrderStatus;
    }

    public static Builder builder() {
        return new Builder();
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

    public String getCustomerId() {
        return customerId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public PaymentOrderStatus getPaymentOrderStatus() {
        return paymentOrderStatus;
    }


    public static final class Builder {
        private String id;
        private String sagaId;
        private String orderId;
        private String customerId;
        private BigDecimal price;
        private Instant createdAt;
        private PaymentOrderStatus paymentOrderStatus;

        private Builder() {
        }

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Builder sagaId(String val) {
            sagaId = val;
            return this;
        }

        public Builder orderId(String val) {
            orderId = val;
            return this;
        }

        public Builder customerId(String val) {
            customerId = val;
            return this;
        }

        public Builder price(BigDecimal val) {
            price = val;
            return this;
        }

        public Builder createdAt(Instant val) {
            createdAt = val;
            return this;
        }

        public Builder paymentOrderStatus(PaymentOrderStatus val) {
            paymentOrderStatus = val;
            return this;
        }

        public PaymentRequest build() {
            return new PaymentRequest(this);
        }
    }
}
