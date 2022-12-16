package com.food.ordering.system.order.service.domain.outbox.scheduler.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.ObjectMapperHelper;
import com.food.ordering.system.order.service.domain.outbox.dto.PaymentOutboxPersistDto;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.repository.PaymentOutboxRepository;
import com.food.ordering.system.ordering.outbox.OutboxStatus;
import com.food.ordering.system.ordering.saga.SagaStatus;
import com.food.ordering.system.ordering.saga.order.SagaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentOutboxHelper {
    private static final Logger log = LoggerFactory.getLogger(PaymentOutboxHelper.class);

    private final PaymentOutboxRepository paymentOutboxRepository;
    private final ObjectMapperHelper objectMapper;

    public PaymentOutboxHelper(PaymentOutboxRepository paymentOutboxRepository,
                               ObjectMapperHelper objectMapper) {
        this.paymentOutboxRepository = paymentOutboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public OrderPaymentOutboxMessage save(OrderPaymentOutboxMessage orderPaymentOutboxMessage) {
        return Optional.ofNullable(paymentOutboxRepository.save(orderPaymentOutboxMessage))
                .map(message -> {
                    log.info("{} persisted with outbox id: {}", message.getClass().getSimpleName(), message.getId());
                    return message;
                })
                .orElseThrow(() -> {
                    String errorMsg = "Could not persist OrderPaymentOutboxMessage with outbox id: %s".formatted(orderPaymentOutboxMessage.getId());
                    log.error(errorMsg);
                    return new OrderDomainException(errorMsg);
                });
    }

    @Transactional
    public void savePaymentOutboxMessage(PaymentOutboxPersistDto paymentOutboxPersistDto) {
        save(
                OrderPaymentOutboxMessage.builder()
                        .id(UUID.randomUUID())
                        .outboxStatus(paymentOutboxPersistDto.outboxStatus())
                        .orderStatus(paymentOutboxPersistDto.orderStatus())
                        .sagaId(paymentOutboxPersistDto.sagaId())
                        .payload(objectMapper.serializePayload(paymentOutboxPersistDto.payload()))
                        .type(SagaConstants.ORDER_SAGA_NAME)
                        .createdAt(paymentOutboxPersistDto.payload().getCreatedAt())
                        .sagaStatus(paymentOutboxPersistDto.sagaStatus())
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public List<OrderPaymentOutboxMessage> getPaymentOutboxMessage(OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        return paymentOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(SagaConstants.ORDER_SAGA_NAME, outboxStatus, sagaStatus);
    }

    @Transactional(readOnly = true)
    public Optional<OrderPaymentOutboxMessage> getPaymentOutboxMessage(UUID sagaId, SagaStatus... sagaStatus) {
        return paymentOutboxRepository.findByTypeAndSagaIdAndSagaStatus(SagaConstants.ORDER_SAGA_NAME, sagaId, sagaStatus);
    }

    @Transactional
    public void deleteByTypeAndOutboxStatusAndSagaStatus(OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        paymentOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatus(SagaConstants.ORDER_SAGA_NAME, outboxStatus, sagaStatus);
    }
}
