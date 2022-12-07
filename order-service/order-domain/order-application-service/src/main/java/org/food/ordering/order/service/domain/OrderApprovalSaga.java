package org.food.ordering.order.service.domain;

import org.food.ordering.domain.OrderDomainService;
import org.food.ordering.domain.entity.Order;
import org.food.ordering.domain.event.EmptyEvent;
import org.food.ordering.domain.event.OrderCancelledEvent;
import org.food.ordering.order.service.domain.dto.message.RestaurantApprovalResponse;
import org.food.ordering.saga.SagaStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse, EmptyEvent, OrderCancelledEvent> {
    private static final Logger log = LoggerFactory.getLogger(OrderApprovalSaga.class);

    private final OrderDomainService orderDomainService;
    private final SagaUtils sagaUtils;

    public OrderApprovalSaga(OrderDomainService orderDomainService,
                             SagaUtils sagaUtils) {
        this.orderDomainService = orderDomainService;
        this.sagaUtils = sagaUtils;
    }


    @Override
    @Transactional
    public EmptyEvent process(RestaurantApprovalResponse response) {
        final Order order = sagaUtils.findOrder(response.getOrderId());
        orderDomainService.approveOrder(order);
        sagaUtils.save(order);
        log.info("Order approved for order with id: {}", response.getOrderId());
        return EmptyEvent.INSTANCE;
    }

    @Override
    @Transactional
    public OrderCancelledEvent rollback(RestaurantApprovalResponse response) {
        final Order order = sagaUtils.findOrder(response.getOrderId());
        log.info("Order cancelled for order with id: {}", response.getOrderId());
        OrderCancelledEvent orderCancelledEvent = orderDomainService.cancelOrderPayment(order, response.getFailureMessages());
        sagaUtils.save(order);
        return orderCancelledEvent;
    }
}
