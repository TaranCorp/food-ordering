package com.food.ordering.system.payment.service.domain.outbox.scheduler;

import com.food.ordering.system.ordering.outbox.OutboxScheduler;
import com.food.ordering.system.ordering.outbox.OutboxStatus;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class PaymentOutboxScheduler implements OutboxScheduler {
    private static final Logger log = LoggerFactory.getLogger(PaymentOutboxScheduler.class);

    private final OrderOutboxHelper outboxHelper;
    private final PaymentResponseMessagePublisher publisher;

    public PaymentOutboxScheduler(OrderOutboxHelper outboxHelper,
                                  PaymentResponseMessagePublisher publisher) {
        this.outboxHelper = outboxHelper;
        this.publisher = publisher;
    }

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${outbox-scheduler-fixed-rate}",
            initialDelayString = "${outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        List<OrderOutboxMessage> orderOutboxMessages = outboxHelper.getByOutboxStatus(OutboxStatus.STARTED);

        if (!orderOutboxMessages.isEmpty()) {
            log.info("Received %s OrderOutboxMessages with ids %s, sending to kafka".formatted(
                    orderOutboxMessages.size(),
                    orderOutboxMessages.stream().map(OrderOutboxMessage::id).toList()
            ));
            orderOutboxMessages.forEach(orderOutboxMessage -> publisher.publish(
                    orderOutboxMessage,
                    outboxHelper.getOutboxCallback()
            ));
            log.info("{} PaymentOutboxMessages sent to kafka", orderOutboxMessages.size());
        }
    }
}
