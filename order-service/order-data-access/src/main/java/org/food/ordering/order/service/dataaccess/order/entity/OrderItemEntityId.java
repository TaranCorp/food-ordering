package org.food.ordering.order.service.dataaccess.order.entity;

import java.io.Serializable;
import java.util.Objects;

public class OrderItemEntityId implements Serializable {

    private Long id;
    private OrderEntity order;

    public OrderItemEntityId() {
    }

    public OrderItemEntityId(Long id, OrderEntity order) {
        this.id = id;
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemEntityId that = (OrderItemEntityId) o;
        return Objects.equals(id, that.id) && Objects.equals(order, that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, order);
    }
}
