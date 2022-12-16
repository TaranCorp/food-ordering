package com.food.ordering.system.order.service.domain.outbox.scheduler.approval;

import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher;
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
class RestaurantApprovalOutboxScheduler implements OutboxScheduler {
    private static final Logger log = LoggerFactory.getLogger(RestaurantApprovalOutboxScheduler.class);

    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final RestaurantApprovalRequestMessagePublisher publisher;

    RestaurantApprovalOutboxScheduler(ApprovalOutboxHelper approvalOutboxHelper,
                                      RestaurantApprovalRequestMessagePublisher publisher) {
        this.approvalOutboxHelper = approvalOutboxHelper;
        this.publisher = publisher;
    }

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${order-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        getApprovalOutboxMessages().forEach(message -> {
            log.info("Publishing OrderApprovalOutboxMessage for outbox id: {}", message.getId());
            publisher.publish(message, this::getCallback);
        });
    }

    private void getCallback(OrderApprovalOutboxMessage orderApprovalOutboxMessage, OutboxStatus outboxStatus) {
        orderApprovalOutboxMessage.setOutboxStatus(outboxStatus);
        approvalOutboxHelper.save(orderApprovalOutboxMessage);
        log.info("OrderApprovalOutboxMessage is updated with outbox status: {}", outboxStatus.name());
    }

    private List<OrderApprovalOutboxMessage> getApprovalOutboxMessages() {
        return approvalOutboxHelper.getApprovalOutboxMessages(OutboxStatus.STARTED, SagaStatus.PROCESSING);
    }
}
