package com.food.ordering.system.payment.service.domain.outbox.model;

import com.food.ordering.system.domain.valueobject.PaymentStatus;
import com.food.ordering.system.ordering.outbox.OutboxStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

public class OrderOutboxMessage {
    private UUID id;
    private UUID sagaId;
    private ZonedDateTime createdAt;
    private ZonedDateTime processedAt;
    private String type;
    private String payload;
    private OutboxStatus outboxStatus;
    private PaymentStatus paymentStatus;
    private int version;

    public OrderOutboxMessage(UUID id,
                              UUID sagaId,
                              ZonedDateTime createdAt,
                              ZonedDateTime processedAt,
                              String type,
                              String payload,
                              OutboxStatus outboxStatus,
                              PaymentStatus paymentStatus,
                              int version) {
        this.id = id;
        this.sagaId = sagaId;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
        this.type = type;
        this.payload = payload;
        this.outboxStatus = outboxStatus;
        this.paymentStatus = paymentStatus;
        this.version = version;
    }

    public UUID id() {
        return id;
    }

    public UUID sagaId() {
        return sagaId;
    }

    public ZonedDateTime createdAt() {
        return createdAt;
    }

    public ZonedDateTime processedAt() {
        return processedAt;
    }

    public String type() {
        return type;
    }

    public String payload() {
        return payload;
    }

    public OutboxStatus outboxStatus() {
        return outboxStatus;
    }

    public PaymentStatus paymentStatus() {
        return paymentStatus;
    }

    public int version() {
        return version;
    }

    public static Builder builder() {
        return new Builder();
    }

    public OrderOutboxMessage updateOutboxStatus(OutboxStatus outboxStatus) {
        this.outboxStatus = outboxStatus;
        return this;
    }

    public static final class Builder {
        private UUID id;
        private UUID sagaId;
        private ZonedDateTime createdAt;
        private ZonedDateTime processedAt;
        private String type;
        private String payload;
        private OutboxStatus outboxStatus;
        private PaymentStatus paymentStatus;
        private int version;

        private Builder() {
        }

        public Builder id(UUID val) {
            id = val;
            return this;
        }

        public Builder sagaId(UUID val) {
            sagaId = val;
            return this;
        }

        public Builder createdAt(ZonedDateTime val) {
            createdAt = val;
            return this;
        }

        public Builder processedAt(ZonedDateTime val) {
            processedAt = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Builder payload(String val) {
            payload = val;
            return this;
        }

        public Builder outboxStatus(OutboxStatus val) {
            outboxStatus = val;
            return this;
        }

        public Builder paymentStatus(PaymentStatus val) {
            paymentStatus = val;
            return this;
        }

        public Builder version(int val) {
            version = val;
            return this;
        }

        public OrderOutboxMessage build() {
            return new OrderOutboxMessage(
                    id,
                    sagaId,
                    createdAt,
                    processedAt,
                    type,
                    payload,
                    outboxStatus,
                    paymentStatus,
                    version
            );
        }
    }
}
