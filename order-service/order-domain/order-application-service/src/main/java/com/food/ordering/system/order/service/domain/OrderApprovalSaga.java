package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.outbox.dto.PaymentOutboxPersistDto;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.system.order.service.domain.outbox.scheduler.approval.ApprovalOutboxHelper;
import com.food.ordering.system.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.food.ordering.system.ordering.outbox.OutboxStatus;
import com.food.ordering.system.ordering.saga.SagaStatus;
import com.food.ordering.system.ordering.saga.SagaStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse> {
    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final OrderDataMapper orderDataMapper;

    public OrderApprovalSaga(OrderDomainService orderDomainService,
                             OrderSagaHelper orderSagaHelper,
                             ApprovalOutboxHelper approvalOutboxHelper,
                             PaymentOutboxHelper paymentOutboxHelper,
                             OrderDataMapper orderDataMapper) {
        this.orderDomainService = orderDomainService;
        this.orderSagaHelper = orderSagaHelper;
        this.approvalOutboxHelper = approvalOutboxHelper;
        this.paymentOutboxHelper = paymentOutboxHelper;
        this.orderDataMapper = orderDataMapper;
    }

    @Override
    @Transactional
    public void process(RestaurantApprovalResponse restaurantApprovalResponse) {
        getOptionalApprovalOutboxMessage(restaurantApprovalResponse)
                .ifPresentOrElse(
                        approvalMessage -> continueApprovalProcess(restaurantApprovalResponse, approvalMessage),
                        () -> log.info("An outbox message with saga id: {} is already processed", restaurantApprovalResponse.getSagaId())
        );
    }

    private Optional<OrderApprovalOutboxMessage> getOptionalApprovalOutboxMessage(RestaurantApprovalResponse restaurantApprovalResponse) {
        return approvalOutboxHelper.getApprovalOutboxMessage(
                UUID.fromString(restaurantApprovalResponse.getSagaId()),
                SagaStatus.PROCESSING
        );
    }

    private void continueApprovalProcess(RestaurantApprovalResponse restaurantApprovalResponse, OrderApprovalOutboxMessage approvalMessage) {
        final Order order = getApprovedOrder(restaurantApprovalResponse);
        final OrderStatus orderStatus = order.getOrderStatus();
        final SagaStatus sagaStatus = orderSagaHelper.sagaStatusFromOrderStatus(orderStatus);

        approvalOutboxHelper.saveUpdatedApprovalOutboxMessage(approvalMessage, orderStatus, sagaStatus);
        paymentOutboxHelper.saveUpdatedPaymentOutboxMessage(restaurantApprovalResponse.getSagaId(), orderStatus, sagaStatus);

        log.info("Order with id: {} is approved", order.getId().getValue());;
    }

    private Order getApprovedOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Approving order with id: {}", restaurantApprovalResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
        orderDomainService.approveOrder(order);
        orderSagaHelper.saveOrder(order);
        return order;
    }

    @Override
    @Transactional
    public void rollback(RestaurantApprovalResponse restaurantApprovalResponse) {
        getOptionalApprovalOutboxMessage(restaurantApprovalResponse)
                .ifPresentOrElse(
                        approvalMessage -> continueRollBackOperation(approvalMessage, restaurantApprovalResponse),
                        () -> log.info("An outbox message with saga id: {} is already processed", restaurantApprovalResponse.getSagaId())
                );
    }

    private void continueRollBackOperation(OrderApprovalOutboxMessage orderApprovalOutboxMessage, RestaurantApprovalResponse restaurantApprovalResponse) {
        final OrderCancelledEvent orderCancelledEvent = processOrderCancelling(restaurantApprovalResponse);
        final OrderStatus orderStatus = orderCancelledEvent.getOrder().getOrderStatus();
        final SagaStatus sagaStatus = orderSagaHelper.sagaStatusFromOrderStatus(orderStatus);

        approvalOutboxHelper.saveUpdatedApprovalOutboxMessage(orderApprovalOutboxMessage, orderStatus, sagaStatus);
        paymentOutboxHelper.savePaymentOutboxMessage(
                new PaymentOutboxPersistDto(
                        orderDataMapper.orderPaymentEventPayloadFromOrderCancelledEvent(orderCancelledEvent),
                        orderStatus,
                        OutboxStatus.STARTED,
                        sagaStatus,
                        UUID.fromString(restaurantApprovalResponse.getSagaId())
                )
        );
//        paymentOutboxHelper.saveUpdatedPaymentOutboxMessage(orderApprovalOutboxMessage.getSagaId().toString(), orderStatus, sagaStatus);

        log.info("Order with id: {} is cancelling", orderCancelledEvent.getOrder().getId().getValue());
    }

    private OrderCancelledEvent processOrderCancelling(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Cancelling order with id: {}", restaurantApprovalResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
        OrderCancelledEvent domainEvent = orderDomainService.cancelOrderPayment(order, restaurantApprovalResponse.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        return domainEvent;
    }
}
