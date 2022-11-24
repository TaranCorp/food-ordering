package org.food.ordering.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.food.ordering.domain.entity.Order;
import org.food.ordering.domain.event.OrderCreatedEvent;
import org.food.ordering.order.service.domain.dto.create.CreateOrderCommand;
import org.food.ordering.order.service.domain.dto.create.CreateOrderResponse;
import org.food.ordering.order.service.domain.mapper.OrderDataMapper;
import org.food.ordering.order.service.domain.port.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderCreateCommandHandler {

    private final OrderCreateHelper orderCreateHelper;
    private final OrderDataMapper orderDataMapper;
    private final OrderCreatedPaymentRequestMessagePublisher publisher;

    OrderCreateCommandHandler(OrderCreateHelper orderCreateHelper, OrderDataMapper orderDataMapper, OrderCreatedPaymentRequestMessagePublisher publisher) {
        this.orderCreateHelper = orderCreateHelper;
        this.orderDataMapper = orderDataMapper;
        this.publisher = publisher;
    }

    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        OrderCreatedEvent orderCreatedEvent = orderCreateHelper.persistOrder(createOrderCommand);
        Order order = orderCreatedEvent.getOrder();
        log.info("Order is created with id: {}", order.getId().getValue());
        publisher.publish(orderCreatedEvent);
        return orderDataMapper.createOrderResponseFromOrder(order, "Order created successfully");
    }
}
