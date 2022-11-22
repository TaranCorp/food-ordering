package org.food.ordering.domain;

import org.food.ordering.domain.entity.Order;
import org.food.ordering.domain.entity.Restaurant;
import org.food.ordering.domain.event.OrderCancelledEvent;
import org.food.ordering.domain.event.OrderCreatedEvent;
import org.food.ordering.domain.event.OrderPaidEvent;

import java.util.List;

public interface OrderDomainService {
    OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant);

    OrderPaidEvent payOrder(Order order);

    void approveOrder(Order order);

    OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages);

    void cancelOrder(Order order, List<String> failureMessages);
}
