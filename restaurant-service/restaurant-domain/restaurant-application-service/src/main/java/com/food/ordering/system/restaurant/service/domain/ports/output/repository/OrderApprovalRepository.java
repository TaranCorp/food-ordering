package org.food.ordering.restaurant.service.domain.ports.output.repository;

import org.food.ordering.restaurant.service.domain.entity.OrderApproval;

public interface OrderApprovalRepository {
    OrderApproval save(OrderApproval orderApproval);
}
