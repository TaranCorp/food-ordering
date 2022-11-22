package org.food.ordering.order.service.domain.port.output.message.publisher.payment;

import org.food.ordering.domain.event.OrderCancelledEvent;
import org.food.ordering.domain.event.publisher.DomainEventPublisher;

public interface OrderCancelledPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCancelledEvent> {
}
