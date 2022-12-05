package org.food.ordering.restaurant.service.domain.port.output.message.publisher;

import org.food.ordering.domain.event.publisher.DomainEventPublisher;
import org.food.ordering.restaurant.service.domain.event.OrderRejectedEvent;

public interface OrderRejectedMessagePublisher extends DomainEventPublisher<OrderRejectedEvent> {
}
