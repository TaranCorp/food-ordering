package org.food.ordering.order.service.dataaccess.order.adapter;

import org.food.ordering.domain.entity.Order;
import org.food.ordering.domain.valueobject.TrackingId;
import org.food.ordering.order.service.dataaccess.order.mapper.OrderDataAccessMapper;
import org.food.ordering.order.service.dataaccess.order.repository.OrderJpaRepository;
import org.food.ordering.order.service.domain.port.output.repository.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository repository;
    private final OrderDataAccessMapper mapper;

    public OrderRepositoryImpl(OrderJpaRepository repository, OrderDataAccessMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Order save(Order order) {
        return mapper.orderFromOrderEntity(
                repository.save(
                        mapper.orderEntityFromOrder(order)
                )
        );
    }

    @Override
    public Optional<Order> findByTrackingId(TrackingId trackingId) {
        return repository.findByTrackingId(trackingId.getValue())
                .map(mapper::orderFromOrderEntity);
    }
}
