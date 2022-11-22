package org.food.ordering.order.service.domain.dto.message;

import org.food.ordering.domain.valueobject.OrderApprovalStatus;

import java.time.Instant;
import java.util.List;

public class RestaurantApprovalResponse {
    private String id;
    private String sagaId;
    private String orderId;
    private String restaurantId;
    private Instant createdAt;
    private OrderApprovalStatus orderApprovalStatus;
    private List<String> failureMessages;

    public RestaurantApprovalResponse(String id, String sagaId, String orderId, String restaurantId, Instant createdAt, OrderApprovalStatus orderApprovalStatus, List<String> failureMessages) {
        this.id = id;
        this.sagaId = sagaId;
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.createdAt = createdAt;
        this.orderApprovalStatus = orderApprovalStatus;
        this.failureMessages = failureMessages;
    }

    public String getId() {
        return id;
    }

    public String getSagaId() {
        return sagaId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public OrderApprovalStatus getOrderApprovalStatus() {
        return orderApprovalStatus;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }
}
