package org.food.ordering.order.service.domain.port.output.message.publisher.restaurantapproval;

import org.food.ordering.domain.event.OrderPaidEvent;
import org.food.ordering.domain.event.publisher.DomainEventPublisher;

public interface OrderPaidRestaurantRequestMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {
}
