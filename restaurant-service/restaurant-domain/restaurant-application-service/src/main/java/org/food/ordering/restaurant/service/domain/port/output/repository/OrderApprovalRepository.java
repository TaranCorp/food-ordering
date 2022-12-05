package org.food.ordering.restaurant.service.domain.port.output.repository;

import org.food.ordering.restaurant.service.domain.entity.OrderApproval;

public interface OrderApprovalRepository {
    OrderApproval save(OrderApproval orderApproval);
}
