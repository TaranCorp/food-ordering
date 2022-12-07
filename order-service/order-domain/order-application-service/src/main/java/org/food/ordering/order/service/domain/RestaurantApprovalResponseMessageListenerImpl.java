package org.food.ordering.order.service.domain;

import org.food.ordering.domain.event.OrderCancelledEvent;
import org.food.ordering.order.service.domain.dto.message.RestaurantApprovalResponse;
import org.food.ordering.order.service.domain.port.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import org.food.ordering.order.service.domain.port.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class RestaurantApprovalResponseMessageListenerImpl implements RestaurantApprovalResponseMessageListener {
    private static final Logger log = LoggerFactory.getLogger(RestaurantApprovalResponseMessageListenerImpl.class);

    private final OrderApprovalSaga orderApprovalSaga;
    private final OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher;

    public RestaurantApprovalResponseMessageListenerImpl(OrderApprovalSaga orderApprovalSaga,
                                                         OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher) {
        this.orderApprovalSaga = orderApprovalSaga;
        this.orderCancelledPaymentRequestMessagePublisher = orderCancelledPaymentRequestMessagePublisher;
    }

    @Override
    public void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse) {
        orderApprovalSaga.process(restaurantApprovalResponse);
        log.info("Order is approved for order with id: {}", restaurantApprovalResponse.getOrderId());
    }

    @Override
    public void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse) {
        OrderCancelledEvent orderCancelledEvent = orderApprovalSaga.rollback(restaurantApprovalResponse);
        log.info("Publishing order cancelled event for order with id: {}", restaurantApprovalResponse.getOrderId());
        orderCancelledPaymentRequestMessagePublisher.publish(orderCancelledEvent);
    }
}
