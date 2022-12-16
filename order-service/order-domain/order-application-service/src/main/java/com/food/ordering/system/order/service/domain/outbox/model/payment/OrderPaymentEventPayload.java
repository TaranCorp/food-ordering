package com.food.ordering.system.order.service.domain.outbox.model.payment;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class OrderPaymentEventPayload {
    private String orderId;
    private String customerId;
    private BigDecimal price;
    private ZonedDateTime createdAt;
    private String paymentOrderStatus;

    public OrderPaymentEventPayload(String orderId,
                                    String customerId,
                                    BigDecimal price,
                                    ZonedDateTime createdAt,
                                    String paymentOrderStatus) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.price = price;
        this.createdAt = createdAt;
        this.paymentOrderStatus = paymentOrderStatus;
    }

    private OrderPaymentEventPayload(Builder builder) {
        orderId = builder.orderId;
        customerId = builder.customerId;
        price = builder.price;
        createdAt = builder.createdAt;
        paymentOrderStatus = builder.paymentOrderStatus;
    }

    public static Builder builder() {
        return new Builder();
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

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public String getPaymentOrderStatus() {
        return paymentOrderStatus;
    }


    public static final class Builder {
        private String orderId;
        private String customerId;
        private BigDecimal price;
        private ZonedDateTime createdAt;
        private String paymentOrderStatus;

        private Builder() {
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

        public Builder createdAt(ZonedDateTime val) {
            createdAt = val;
            return this;
        }

        public Builder paymentOrderStatus(String val) {
            paymentOrderStatus = val;
            return this;
        }

        public OrderPaymentEventPayload build() {
            return new OrderPaymentEventPayload(this);
        }
    }
}

