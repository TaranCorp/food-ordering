package org.food.ordering.order.service.domain.port.output.message.publisher.payment;

import org.food.ordering.domain.event.OrderCreatedEvent;
import org.food.ordering.domain.event.publisher.DomainEventPublisher;

public interface OrderCreatedPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCreatedEvent> {

}
