package org.food.ordering.order.service.domain.port.output.repository;

import org.food.ordering.domain.entity.Order;
import org.food.ordering.domain.valueobject.TrackingId;

import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findByTrackingId(TrackingId trackingId);
}
