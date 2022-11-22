package org.food.ordering.order.service.domain.dto.track;

import org.food.ordering.domain.valueobject.OrderStatus;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class TrackOrderResponse {

    @NotNull
    private final UUID orderTrackingId;

    @NotNull
    private final OrderStatus orderStatus;

    private final List<String> failureMessages;

    public TrackOrderResponse(UUID orderTrackingId, OrderStatus orderStatus, List<String> failureMessages) {
        this.orderTrackingId = orderTrackingId;
        this.orderStatus = orderStatus;
        this.failureMessages = failureMessages;
    }

    public UUID getOrderTrackingId() {
        return orderTrackingId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }
}
