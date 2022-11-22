package org.food.ordering.domain.event;

import org.food.ordering.domain.entity.Order;

public class OrderPaidEvent extends OrderEvent {
    public OrderPaidEvent(Order order) {
        super(order);
    }
}
