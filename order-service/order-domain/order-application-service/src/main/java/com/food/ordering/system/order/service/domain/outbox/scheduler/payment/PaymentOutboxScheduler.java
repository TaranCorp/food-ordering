package com.food.ordering.system.order.service.domain.outbox.scheduler.payment;

import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import com.food.ordering.system.ordering.outbox.OutboxScheduler;
import com.food.ordering.system.ordering.outbox.OutboxStatus;
import com.food.ordering.system.ordering.saga.SagaStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
class PaymentOutboxScheduler implements OutboxScheduler {
    private static final Logger log = LoggerFactory.getLogger(PaymentOutboxScheduler.class);

    private final PaymentOutboxHelper paymentOutboxHelper;
    private final PaymentRequestMessagePublisher publisher;

    PaymentOutboxScheduler(PaymentOutboxHelper paymentOutboxHelper,
                           PaymentRequestMessagePublisher publisher) {
        this.paymentOutboxHelper = paymentOutboxHelper;
        this.publisher = publisher;
    }

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
                initialDelayString = "${order-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        getPaymentOutboxMessages().forEach(orderPaymentOutboxMessage -> {
            log.info("Received OrderPaymentOutboxMessage with id: {}", orderPaymentOutboxMessage.getId());
            publisher.publish(orderPaymentOutboxMessage, this::updateOutboxStatus);
        });
    }

    private void updateOutboxStatus(OrderPaymentOutboxMessage orderPaymentOutboxMessage, OutboxStatus outboxStatus) {
        orderPaymentOutboxMessage.setOutboxStatus(outboxStatus);
        paymentOutboxHelper.save(orderPaymentOutboxMessage);
        log.info("OrderPaymentOutboxMessage is updated with outbox status: {}", outboxStatus.name());
    }

    private List<OrderPaymentOutboxMessage> getPaymentOutboxMessages() {
        return paymentOutboxHelper.getPaymentOutboxMessage(OutboxStatus.STARTED, SagaStatus.STARTED, SagaStatus.COMPENSATING);
    }
}
