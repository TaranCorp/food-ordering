package org.food.ordering.restaurant.service.domain;

import org.food.ordering.restaurant.service.domain.entity.Restaurant;
import org.food.ordering.restaurant.service.domain.event.OrderApprovalEvent;

import java.util.List;

public interface RestaurantDomainService {
    OrderApprovalEvent validateOrder(Restaurant restaurant, List<String> failureMessages);
}
