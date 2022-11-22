package org.food.ordering.domain.event;

import org.food.ordering.domain.entity.Order;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public abstract class OrderEvent implements DomainEvent<Order> {
    private final Order order;
    private final ZonedDateTime createdAt;

    protected OrderEvent(Order order) {
        this.order = order;
        this.createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }

    public Order getOrder() {
        return order;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
}
