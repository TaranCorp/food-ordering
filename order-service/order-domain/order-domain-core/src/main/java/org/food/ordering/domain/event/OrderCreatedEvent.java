package org.food.ordering.domain.event;

import org.food.ordering.domain.entity.Order;

public class OrderCreatedEvent extends OrderEvent {
    public OrderCreatedEvent(Order order) {
        super(order);
    }
}
