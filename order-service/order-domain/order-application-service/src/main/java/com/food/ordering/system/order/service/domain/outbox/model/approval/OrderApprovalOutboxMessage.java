package com.food.ordering.system.order.service.domain.outbox.model.approval;

import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.ordering.outbox.OutboxStatus;
import com.food.ordering.system.ordering.saga.SagaStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

public class OrderApprovalOutboxMessage {
    private UUID id;
    private UUID sagaId;
    private ZonedDateTime createdAt;
    private ZonedDateTime processedAt;
    private String type;
    private String payload;
    private SagaStatus sagaStatus;
    private OrderStatus orderStatus;
    private OutboxStatus outboxStatus;
    private int version;

    public OrderApprovalOutboxMessage(UUID id,
                                     UUID sagaId,
                                     ZonedDateTime createdAt,
                                     ZonedDateTime processedAt,
                                     String type,
                                     String payload,
                                     SagaStatus sagaStatus,
                                     OrderStatus orderStatus,
                                     OutboxStatus outboxStatus,
                                     int version) {
        this.id = id;
        this.sagaId = sagaId;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
        this.type = type;
        this.payload = payload;
        this.sagaStatus = sagaStatus;
        this.orderStatus = orderStatus;
        this.outboxStatus = outboxStatus;
        this.version = version;
    }

    private OrderApprovalOutboxMessage(Builder builder) {
        id = builder.id;
        sagaId = builder.sagaId;
        createdAt = builder.createdAt;
        processedAt = builder.processedAt;
        type = builder.type;
        payload = builder.payload;
        sagaStatus = builder.sagaStatus;
        orderStatus = builder.orderStatus;
        outboxStatus = builder.outboxStatus;
        version = builder.version;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() {
        return id;
    }

    public UUID getSagaId() {
        return sagaId;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getProcessedAt() {
        return processedAt;
    }

    public String getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }

    public SagaStatus getSagaStatus() {
        return sagaStatus;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public OutboxStatus getOutboxStatus() {
        return outboxStatus;
    }

    public int getVersion() {
        return version;
    }

    public void setProcessedAt(ZonedDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public void setSagaStatus(SagaStatus sagaStatus) {
        this.sagaStatus = sagaStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setOutboxStatus(OutboxStatus outboxStatus) {
        this.outboxStatus = outboxStatus;
    }

    public static final class Builder {
        private UUID id;
        private UUID sagaId;
        private ZonedDateTime createdAt;
        private ZonedDateTime processedAt;
        private String type;
        private String payload;
        private SagaStatus sagaStatus;
        private OrderStatus orderStatus;
        private OutboxStatus outboxStatus;
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

        public Builder sagaStatus(SagaStatus val) {
            sagaStatus = val;
            return this;
        }

        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder outboxStatus(OutboxStatus val) {
            outboxStatus = val;
            return this;
        }

        public Builder version(int val) {
            version = val;
            return this;
        }

        public OrderApprovalOutboxMessage build() {
            return new OrderApprovalOutboxMessage(this);
        }
    }
}
