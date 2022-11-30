package org.food.ordering.order.service.dataaccess.order.repository;

import org.food.ordering.order.service.dataaccess.order.entity.OrderEntity;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepository extends Repository<OrderEntity, UUID> {
    OrderEntity save(OrderEntity order);

    Optional<OrderEntity> findByTrackingId(UUID trackingId);
}
