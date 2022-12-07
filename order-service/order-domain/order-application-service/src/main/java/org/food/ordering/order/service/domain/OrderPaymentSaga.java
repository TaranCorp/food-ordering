package org.food.ordering.order.service.domain;

import org.food.ordering.domain.OrderDomainService;
import org.food.ordering.domain.entity.Order;
import org.food.ordering.domain.event.EmptyEvent;
import org.food.ordering.domain.event.OrderPaidEvent;
import org.food.ordering.order.service.domain.dto.message.PaymentResponse;
import org.food.ordering.saga.SagaStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderPaymentSaga implements SagaStep<PaymentResponse, OrderPaidEvent, EmptyEvent> {
    private static final Logger log = LoggerFactory.getLogger(OrderPaymentSaga.class);

    private final OrderDomainService orderDomainService;
    private final SagaUtils sagaUtils;

    public OrderPaymentSaga(OrderDomainService orderDomainService,
                            SagaUtils sagaUtils) {
        this.orderDomainService = orderDomainService;
        this.sagaUtils = sagaUtils;
    }

    @Override
    @Transactional
    public OrderPaidEvent process(PaymentResponse response) {
        log.info("Completing payment for order with id: {}", response.getOrderId());
        final Order order = sagaUtils.findOrder(response.getOrderId());
        final OrderPaidEvent orderPaidEvent = orderDomainService.payOrder(order);
        sagaUtils.save(order);
        log.info("Order with id: {} is paid", order.getId().getValue());
        return orderPaidEvent;
    }

    @Override
    @Transactional
    public EmptyEvent rollback(PaymentResponse response) {
        log.info("Cancelling order with id: {}", response.getOrderId());
        final Order order = sagaUtils.findOrder(response.getOrderId());
        orderDomainService.cancelOrder(order, response.getFailureMessages());
        sagaUtils.save(order);
        log.info("Order with id: {} is cancelled", response.getOrderId());
        return EmptyEvent.INSTANCE;
    }
}
