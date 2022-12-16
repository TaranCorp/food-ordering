package com.food.ordering.system.order.service.domain.outbox.scheduler.approval;

import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.ObjectMapperHelper;
import com.food.ordering.system.order.service.domain.outbox.dto.OrderApprovalOutboxPersistDto;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.repository.ApprovalOutboxRepository;
import com.food.ordering.system.ordering.outbox.OutboxStatus;
import com.food.ordering.system.ordering.saga.SagaStatus;
import com.food.ordering.system.ordering.saga.order.SagaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.food.ordering.system.domain.DomainConstants.UTC;

@Component
public class ApprovalOutboxHelper {
    private static final Logger log = LoggerFactory.getLogger(ApprovalOutboxHelper.class);

    private final ApprovalOutboxRepository approvalOutboxRepository;
    private final ObjectMapperHelper objectMapperHelper;

    ApprovalOutboxHelper(ApprovalOutboxRepository approvalOutboxRepository,
                         ObjectMapperHelper objectMapperHelper) {
        this.approvalOutboxRepository = approvalOutboxRepository;
        this.objectMapperHelper = objectMapperHelper;
    }

    @Transactional(readOnly = true)
    public List<OrderApprovalOutboxMessage> getApprovalOutboxMessages(OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        return approvalOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(SagaConstants.ORDER_SAGA_NAME, outboxStatus, sagaStatus);
    }

    @Transactional(readOnly = true)
    public Optional<OrderApprovalOutboxMessage> getApprovalOutboxMessage(UUID sagaId, SagaStatus... sagaStatus) {
        return approvalOutboxRepository.findByTypeAndSagaIdAndSagaStatus(SagaConstants.ORDER_SAGA_NAME, sagaId, sagaStatus);
    }

    @Transactional
    public void save(OrderApprovalOutboxMessage orderApprovalOutboxMessage) {
        final OrderApprovalOutboxMessage message = approvalOutboxRepository.save(orderApprovalOutboxMessage);
        if (message == null) {
            final String errorMsg = "Could not save OrderApprovalOutboxMessage with outbox id: %s".formatted(orderApprovalOutboxMessage.getId());
            log.error(errorMsg);
            throw new OrderDomainException(errorMsg);
        }
        log.info("OrderApprovalOutboxMessage saved with outbox id: {}", orderApprovalOutboxMessage.getId());
    }

    @Transactional
    public void deleteOutboxMessages(OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        approvalOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatus(SagaConstants.ORDER_SAGA_NAME, outboxStatus, sagaStatus);
    }

    @Transactional
    public void saveApprovalOutboxMessage(OrderApprovalOutboxPersistDto persistDto) {
        save(
                OrderApprovalOutboxMessage.builder()
                        .id(UUID.randomUUID())
                        .payload(objectMapperHelper.serializePayload(persistDto))
                        .createdAt(persistDto.payload().getCreatedAt())
                        .type(SagaConstants.ORDER_SAGA_NAME)
                        .orderStatus(persistDto.orderStatus())
                        .sagaStatus(persistDto.sagaStatus())
                        .outboxStatus(persistDto.outboxStatus())
                        .sagaId(persistDto.sagaId())
                        .build()
        );
    }

    @Transactional
    public void saveUpdatedApprovalOutboxMessage(OrderApprovalOutboxMessage approvalMessage, OrderStatus orderStatus, SagaStatus sagaStatus) {
        save(getUpdatedOrderApprovalOutboxMessage(
                approvalMessage,
                orderStatus,
                sagaStatus
            )
        );
    }

    private OrderApprovalOutboxMessage getUpdatedOrderApprovalOutboxMessage(OrderApprovalOutboxMessage approvalMessage, OrderStatus orderStatus, SagaStatus sagaStatus) {
        approvalMessage.setOrderStatus(orderStatus);
        approvalMessage.setSagaStatus(sagaStatus);
        approvalMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
        return approvalMessage;
    }
}
