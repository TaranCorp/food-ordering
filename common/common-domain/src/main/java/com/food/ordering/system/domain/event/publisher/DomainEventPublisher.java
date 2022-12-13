package org.food.ordering.domain.event.publisher;

import org.food.ordering.domain.event.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent> {

    void publish(T domainEvent);
}
