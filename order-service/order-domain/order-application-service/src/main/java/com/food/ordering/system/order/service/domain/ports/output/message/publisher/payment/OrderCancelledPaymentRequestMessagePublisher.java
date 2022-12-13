package org.food.ordering.order.service.domain.ports.output.message.publisher.payment;

import org.food.ordering.domain.event.publisher.DomainEventPublisher;
import org.food.ordering.order.service.domain.event.OrderCancelledEvent;

public interface OrderCancelledPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCancelledEvent> {
}
