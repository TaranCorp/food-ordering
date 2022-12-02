package org.food.ordering.payment.service.domain.ports.output.message.publisher;

import org.food.ordering.domain.event.publisher.DomainEventPublisher;
import org.food.ordering.payment.service.domain.event.PaymentFailedEvent;

public interface PaymentFailedMessagePublisher extends DomainEventPublisher<PaymentFailedEvent> {
}
