package org.food.ordering.order.service.domain;

import org.food.ordering.domain.entity.Order;
import org.food.ordering.domain.exception.OrderNotFoundException;
import org.food.ordering.order.service.domain.port.output.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class SagaUtils {
    private static final Logger log = LoggerFactory.getLogger(SagaUtils.class);

    private final OrderRepository orderRepository;

    SagaUtils(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    Order findOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    String errorMsg = "Cannot find order with id: %s".formatted(orderId);
                    log.error(errorMsg);
                    return new OrderNotFoundException(errorMsg);
                });
    }

    Order save(Order order) {
        return orderRepository.save(order);
    }
}
