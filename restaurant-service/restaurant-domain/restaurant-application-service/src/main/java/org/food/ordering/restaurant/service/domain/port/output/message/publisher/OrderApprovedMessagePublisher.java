package org.food.ordering.restaurant.service.domain.port.output.message.publisher;

import org.food.ordering.domain.event.publisher.DomainEventPublisher;
import org.food.ordering.restaurant.service.domain.event.OrderApprovedEvent;

public interface OrderApprovedMessagePublisher extends DomainEventPublisher<OrderApprovedEvent> {
}
