package org.food.ordering.restaurant.service.domain;

import org.food.ordering.domain.valueobject.OrderApprovalStatus;
import org.food.ordering.restaurant.service.domain.entity.Restaurant;
import org.food.ordering.restaurant.service.domain.event.OrderApprovalEvent;
import org.food.ordering.restaurant.service.domain.event.OrderApprovedEvent;
import org.food.ordering.restaurant.service.domain.event.OrderRejectedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class RestaurantDomainServiceImpl implements RestaurantDomainService {
    private static final Logger log = LoggerFactory.getLogger(RestaurantDomainServiceImpl.class);

    @Override
    public OrderApprovalEvent validateOrder(Restaurant restaurant, List<String> failureMessages) {
        log.info("Validating order with id: {} in restaurant with id: {}", restaurant.getOrderDetail().getId(), restaurant.getId());
        restaurant.validateOrder(failureMessages);

        if (failureMessages.isEmpty()) {
            restaurant.constructOrderApproval(OrderApprovalStatus.APPROVED);
            log.info("Creating OrderApprovedEvent for order id: {}", restaurant.getOrderDetail().getId());
            return new OrderApprovedEvent(
                    restaurant.getOrderApproval(),
                    restaurant.getId(),
                    ZonedDateTime.now(ZoneId.of("UTC"))
            );
        }
        restaurant.constructOrderApproval(OrderApprovalStatus.REJECTED);
        log.info("Creating OrderRejectedEvent for order id: {}", restaurant.getOrderDetail().getId());
        return new OrderRejectedEvent(
                restaurant.getOrderApproval(),
                restaurant.getId(),
                failureMessages,
                ZonedDateTime.now(ZoneId.of("UTC"))
        );
    }
}
