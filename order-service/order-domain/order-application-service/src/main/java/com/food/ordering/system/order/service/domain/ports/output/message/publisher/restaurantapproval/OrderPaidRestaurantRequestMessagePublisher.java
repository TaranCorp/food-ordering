package org.food.ordering.order.service.domain.ports.output.message.publisher.restaurantapproval;

import org.food.ordering.domain.event.publisher.DomainEventPublisher;
import org.food.ordering.order.service.domain.event.OrderPaidEvent;

public interface OrderPaidRestaurantRequestMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {
}
