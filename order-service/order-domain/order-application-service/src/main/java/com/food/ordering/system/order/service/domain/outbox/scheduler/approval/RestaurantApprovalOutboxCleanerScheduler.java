package com.food.ordering.system.order.service.domain.outbox.scheduler.approval;

import com.food.ordering.system.ordering.outbox.OutboxScheduler;
import com.food.ordering.system.ordering.outbox.OutboxStatus;
import com.food.ordering.system.ordering.saga.SagaStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class RestaurantApprovalOutboxCleanerScheduler implements OutboxScheduler {
    private static final Logger log = LoggerFactory.getLogger(RestaurantApprovalOutboxCleanerScheduler.class);

    private final ApprovalOutboxHelper approvalOutboxHelper;

    RestaurantApprovalOutboxCleanerScheduler(ApprovalOutboxHelper approvalOutboxHelper) {
        this.approvalOutboxHelper = approvalOutboxHelper;
    }

    @Override
    @Transactional
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        log.info("Deleting all OrderApprovalOutboxMessages in ending phase");
        approvalOutboxHelper.deleteOutboxMessages(OutboxStatus.COMPLETED, SagaStatus.COMPENSATED, SagaStatus.SUCCEEDED, SagaStatus.FAILED);
    }
}
