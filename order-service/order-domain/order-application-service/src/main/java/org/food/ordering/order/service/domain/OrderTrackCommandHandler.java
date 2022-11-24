package org.food.ordering.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.food.ordering.domain.exception.OrderNotFoundException;
import org.food.ordering.domain.valueobject.TrackingId;
import org.food.ordering.order.service.domain.dto.track.TrackOrderQuery;
import org.food.ordering.order.service.domain.dto.track.TrackOrderResponse;
import org.food.ordering.order.service.domain.mapper.OrderDataMapper;
import org.food.ordering.order.service.domain.port.output.repository.OrderRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class OrderTrackCommandHandler {
    private final OrderRepository orderRepository;
    private final OrderDataMapper orderDataMapper;

    OrderTrackCommandHandler(OrderRepository orderRepository,
                             OrderDataMapper orderDataMapper) {
        this.orderRepository = orderRepository;
        this.orderDataMapper = orderDataMapper;
    }

    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
        UUID orderTrackingId = trackOrderQuery.getOrderTrackingId();

        return orderRepository.findByTrackingId(TrackingId.of(orderTrackingId))
                .map(orderDataMapper::createTrackOrderResponseFromOrder)
                .orElseThrow(() -> {
                    log.warn("Could not find order with tracking id: {}", orderTrackingId);
                    return new OrderNotFoundException("Could not find order with tracking id: " + orderTrackingId);
                });
    }
}
