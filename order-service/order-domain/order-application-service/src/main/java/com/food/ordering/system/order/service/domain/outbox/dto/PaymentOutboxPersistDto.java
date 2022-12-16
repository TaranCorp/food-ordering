package com.food.ordering.system.order.service.domain.outbox.dto;

import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.food.ordering.system.ordering.outbox.OutboxStatus;
import com.food.ordering.system.ordering.saga.SagaStatus;

import java.util.UUID;

public record PaymentOutboxPersistDto (
        OrderPaymentEventPayload payload,
        OrderStatus orderStatus,
        OutboxStatus outboxStatus,
        SagaStatus sagaStatus,
        UUID sagaId
) {}
