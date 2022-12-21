package com.food.ordering.system.payment.service.domain.outbox.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.system.domain.valueobject.PaymentStatus;
import com.food.ordering.system.ordering.outbox.OutboxStatus;
import com.food.ordering.system.ordering.saga.order.SagaConstants;
import com.food.ordering.system.payment.service.domain.exception.PaymentDomainException;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.system.payment.service.domain.ports.output.repository.OrderOutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

import static com.food.ordering.system.domain.DomainConstants.UTC;

@Component
public class OrderOutboxHelper {
    private static final Logger log = LoggerFactory.getLogger(OrderOutboxHelper.class);

    private final OrderOutboxRepository orderOutboxRepository;
    private final ObjectMapper objectMapper;

    public OrderOutboxHelper(OrderOutboxRepository orderOutboxRepository,
                             ObjectMapper objectMapper) {
        this.orderOutboxRepository = orderOutboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public Optional<OrderOutboxMessage> getCompletedOrderOutboxMessagesBySagaIdAndPaymentStatus(UUID sagaId, PaymentStatus paymentStatus) {
        return orderOutboxRepository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
                SagaConstants.ORDER_SAGA_NAME,
                sagaId,
                paymentStatus,
                OutboxStatus.STARTED
        );
    }

    @Transactional(readOnly = true)
    public List<OrderOutboxMessage> getByOutboxStatus(OutboxStatus outboxStatus) {
        return orderOutboxRepository.findByTypeAndOutboxStatus(SagaConstants.ORDER_SAGA_NAME, outboxStatus);
    }

    @Transactional
    public void deleteByOutboxStatus(OutboxStatus outboxStatus) {
        orderOutboxRepository.deleteByTypeAndOutboxStatus(SagaConstants.ORDER_SAGA_NAME, outboxStatus);
    }

    @Transactional
    public OrderOutboxMessage save(OrderEventPayload orderEventPayload,
                                   PaymentStatus paymentStatus,
                                   OutboxStatus outboxStatus,
                                   UUID sagaId) {
        return save(
            OrderOutboxMessage.builder()
                    .id(UUID.randomUUID())
                    .sagaId(sagaId)
                    .outboxStatus(outboxStatus)
                    .paymentStatus(paymentStatus)
                    .processedAt(ZonedDateTime.now(ZoneId.of(UTC)))
                    .payload(createPayload(orderEventPayload))
                    .type(SagaConstants.ORDER_SAGA_NAME)
                    .createdAt(orderEventPayload.getCreatedAt())
                    .build()
        );
    }

    private OrderOutboxMessage save(OrderOutboxMessage orderOutboxMessage) {
        final OrderOutboxMessage persistedOutboxMessage = orderOutboxRepository.save(orderOutboxMessage);
        if (persistedOutboxMessage == null) {
            final String errorMsg = "Could not save OrderOutboxMessage";
            log.error(errorMsg);
            throw new PaymentDomainException(errorMsg);
        }
        log.info("OrderOutboxMessage saved with id: {}", orderOutboxMessage.id());
        return persistedOutboxMessage;
    }

    private String createPayload(OrderEventPayload orderEventPayload) {
        try {
            return objectMapper.writeValueAsString(orderEventPayload);
        } catch (JsonProcessingException e) {
            final String errorMsg = "There was an error while parsing OrderEventPayload to json";
            log.error(errorMsg, e);
            throw new PaymentDomainException(errorMsg, e);
        }
    }

    @Transactional
    public BiConsumer<OrderOutboxMessage, OutboxStatus> getOutboxCallback() {
        return (orderOutboxMessage, outboxStatus) -> {
            save(orderOutboxMessage.updateOutboxStatus(outboxStatus));
            log.info("Order outbox table status is updated as: {}", outboxStatus.name());
        };
    }
}
