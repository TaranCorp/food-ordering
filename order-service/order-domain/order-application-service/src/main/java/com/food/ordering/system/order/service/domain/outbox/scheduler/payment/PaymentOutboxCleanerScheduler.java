package com.food.ordering.system.order.service.domain.outbox.scheduler.payment;

import com.food.ordering.system.ordering.outbox.OutboxScheduler;
import com.food.ordering.system.ordering.outbox.OutboxStatus;
import com.food.ordering.system.ordering.saga.SagaStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class PaymentOutboxCleanerScheduler implements OutboxScheduler {
    private static final Logger log = LoggerFactory.getLogger(PaymentOutboxCleanerScheduler.class);

    private final PaymentOutboxHelper paymentOutboxHelper;

    PaymentOutboxCleanerScheduler(PaymentOutboxHelper paymentOutboxHelper) {
        this.paymentOutboxHelper = paymentOutboxHelper;
    }

    @Override
    @Transactional
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        log.info("Deleting all Outbox event in end phase");
        paymentOutboxHelper.deleteByTypeAndOutboxStatusAndSagaStatus(OutboxStatus.COMPLETED, SagaStatus.FAILED, SagaStatus.SUCCEEDED, SagaStatus.COMPENSATED);
    }
}
