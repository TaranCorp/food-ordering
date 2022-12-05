package org.food.ordering.restaurant.service.dataaccess.restaurant.entity;

import org.food.ordering.domain.valueobject.OrderApprovalStatus;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "order_approval", schema = "restaurant")
public class OrderApprovalEntity {

    @Id
    private UUID id;
    private UUID restaurantId;
    private UUID orderId;
    @Enumerated(EnumType.STRING)
    private OrderApprovalStatus orderApprovalStatus;

    public OrderApprovalEntity() {
    }

    public OrderApprovalEntity(UUID id, UUID restaurantId, UUID orderId, OrderApprovalStatus orderApprovalStatus) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.orderId = orderId;
        this.orderApprovalStatus = orderApprovalStatus;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(UUID restaurantId) {
        this.restaurantId = restaurantId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public OrderApprovalStatus getOrderApprovalStatus() {
        return orderApprovalStatus;
    }

    public void setOrderApprovalStatus(OrderApprovalStatus orderApprovalStatus) {
        this.orderApprovalStatus = orderApprovalStatus;
    }
}

