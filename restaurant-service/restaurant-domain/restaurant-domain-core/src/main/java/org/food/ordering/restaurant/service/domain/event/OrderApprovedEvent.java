package org.food.ordering.restaurant.service.domain.event;

import org.food.ordering.domain.valueobject.RestaurantId;
import org.food.ordering.restaurant.service.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.Collections;

public class OrderApprovedEvent extends OrderApprovalEvent {
    public OrderApprovedEvent(OrderApproval orderApproval, RestaurantId restaurantId, ZonedDateTime createdAt) {
        super(orderApproval, restaurantId, Collections.emptyList(), createdAt);
    }
}