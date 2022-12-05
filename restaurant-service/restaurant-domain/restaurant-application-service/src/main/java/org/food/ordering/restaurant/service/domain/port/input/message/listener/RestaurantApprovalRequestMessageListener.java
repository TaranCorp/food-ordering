package org.food.ordering.restaurant.service.domain.port.input.message.listener;

import org.food.ordering.restaurant.service.domain.dto.RestaurantApprovalRequest;

public interface RestaurantApprovalRequestMessageListener {
    void approveOrder(RestaurantApprovalRequest restaurantApprovalRequest);
}
