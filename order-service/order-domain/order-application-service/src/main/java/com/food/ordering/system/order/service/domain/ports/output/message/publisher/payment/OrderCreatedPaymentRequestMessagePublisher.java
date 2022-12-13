package org.food.ordering.order.service.domain.ports.output.message.publisher.payment;

import org.food.ordering.domain.event.publisher.DomainEventPublisher;
import org.food.ordering.order.service.domain.event.OrderCreatedEvent;

public interface OrderCreatedPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCreatedEvent> {
}
