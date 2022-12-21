package com.food.ordering.system.payment.service.domain.outbox.scheduler;

import com.food.ordering.system.ordering.outbox.OutboxScheduler;
import com.food.ordering.system.ordering.outbox.OutboxStatus;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
class PaymentOutboxCleanerScheduler implements OutboxScheduler {
    private static final Logger log = LoggerFactory.getLogger(PaymentOutboxCleanerScheduler.class);

    private final OrderOutboxHelper outboxHelper;

    PaymentOutboxCleanerScheduler(OrderOutboxHelper outboxHelper) {
        this.outboxHelper = outboxHelper;
    }

    @Override
    @Transactional
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        final List<OrderOutboxMessage> outboxMessages = outboxHelper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED).get();
        if (!outboxMessages.isEmpty()) {
            log.info("{} PaymentOutboxMessages with outbox status COMPLETED, deleting", outboxMessages.size());
            outboxHelper.deleteOrderOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED);
        }
    }
}
