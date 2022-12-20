package com.food.ordering.system.order.service.dataaccess.outbox.payment.repository;

import com.food.ordering.system.order.service.dataaccess.outbox.payment.entity.PaymentOutboxEntity;
import com.food.ordering.system.ordering.outbox.OutboxStatus;
import com.food.ordering.system.ordering.saga.SagaStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentOutboxJpaRepository extends JpaRepository<PaymentOutboxEntity, UUID> {
    PaymentOutboxEntity save(PaymentOutboxEntity PaymentOutboxEntity);

    List<PaymentOutboxEntity> findByTypeAndOutboxStatusAndSagaStatusIn(String type, OutboxStatus outboxStatus, SagaStatus... sagaStatus);

    Optional<PaymentOutboxEntity> findByTypeAndSagaIdAndSagaStatusIn(String type, UUID sagaId, SagaStatus... sagaStatus);

    void deleteByTypeAndOutboxStatusAndSagaStatusIn(String type, OutboxStatus outboxStatus, SagaStatus... sagaStatus);
}
