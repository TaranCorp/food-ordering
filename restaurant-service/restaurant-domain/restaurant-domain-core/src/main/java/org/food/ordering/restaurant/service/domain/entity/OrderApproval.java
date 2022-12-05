package org.food.ordering.restaurant.service.domain.entity;

import org.food.ordering.domain.entity.BaseEntity;
import org.food.ordering.domain.valueobject.OrderApprovalStatus;
import org.food.ordering.domain.valueobject.OrderId;
import org.food.ordering.domain.valueobject.RestaurantId;
import org.food.ordering.restaurant.service.domain.valueobject.OrderApprovalId;

public class OrderApproval extends BaseEntity<OrderApprovalId> {
    private final RestaurantId restaurantId;
    private final OrderId orderId;
    private final OrderApprovalStatus approvalStatus;

    public OrderApproval(OrderApprovalId orderApprovalId, RestaurantId restaurantId, OrderId orderId, OrderApprovalStatus approvalStatus) {
        super.setId(orderApprovalId);
        this.restaurantId = restaurantId;
        this.orderId = orderId;
        this.approvalStatus = approvalStatus;
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public OrderApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }
}
