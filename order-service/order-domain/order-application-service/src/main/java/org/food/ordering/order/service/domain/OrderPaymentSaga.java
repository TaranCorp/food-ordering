package org.food.ordering.order.service.domain;

import org.food.ordering.domain.OrderDomainService;
import org.food.ordering.domain.entity.Order;
import org.food.ordering.domain.event.EmptyEvent;
import org.food.ordering.domain.event.OrderPaidEvent;
import org.food.ordering.domain.exception.OrderNotFoundException;
import org.food.ordering.order.service.domain.dto.message.PaymentResponse;
import org.food.ordering.order.service.domain.port.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import org.food.ordering.order.service.domain.port.output.repository.OrderRepository;
import org.food.ordering.saga.SagaStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderPaymentSaga implements SagaStep<PaymentResponse, OrderPaidEvent, EmptyEvent> {
    private static final Logger log = LoggerFactory.getLogger(OrderPaymentSaga.class);

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher;

    public OrderPaymentSaga(OrderDomainService orderDomainService,
                            OrderRepository orderRepository,
                            OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher) {
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.orderPaidRestaurantRequestMessagePublisher = orderPaidRestaurantRequestMessagePublisher;
    }

    @Override
    @Transactional
    public OrderPaidEvent process(PaymentResponse response) {
        log.info("Completing payment for order with id: {}", response.getOrderId());
        final Order order = findOrder(response.getOrderId());
        final OrderPaidEvent orderPaidEvent = orderDomainService.payOrder(order);
        orderRepository.save(order);
        log.info("Order with id: {} is paid", order.getId().getValue());
        return orderPaidEvent;
    }

    @Override
    @Transactional
    public EmptyEvent rollback(PaymentResponse response) {
        log.info("Cancelling order with id: {}", response.getOrderId());
        final Order order = findOrder(response.getOrderId());
        orderDomainService.cancelOrder(order, response.getFailureMessages());
        orderRepository.save(order);
        log.info("Order with id: {} is cancelled", response.getOrderId());
        return EmptyEvent.INSTANCE;
    }

    private Order findOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    String errorMsg = "Cannot find order with id: %s".formatted(orderId);
                    log.error(errorMsg);
                    return new OrderNotFoundException(errorMsg);
                });
    }
}
