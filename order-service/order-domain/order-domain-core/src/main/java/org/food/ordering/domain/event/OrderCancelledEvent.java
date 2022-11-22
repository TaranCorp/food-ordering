package org.food.ordering.domain.event;

import org.food.ordering.domain.entity.Order;

public class OrderCancelledEvent extends OrderEvent {
    public OrderCancelledEvent(Order order) {
        super(order);
    }
}
