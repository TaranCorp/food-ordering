package org.food.ordering.restaurant.service.domain;

import org.food.ordering.restaurant.service.domain.dto.RestaurantApprovalRequest;
import org.food.ordering.restaurant.service.domain.event.OrderApprovalEvent;
import org.food.ordering.restaurant.service.domain.event.OrderApprovedEvent;
import org.food.ordering.restaurant.service.domain.event.OrderRejectedEvent;
import org.food.ordering.restaurant.service.domain.port.input.message.listener.RestaurantApprovalRequestMessageListener;
import org.food.ordering.restaurant.service.domain.port.output.message.publisher.OrderApprovedMessagePublisher;
import org.food.ordering.restaurant.service.domain.port.output.message.publisher.OrderRejectedMessagePublisher;
import org.springframework.stereotype.Service;

@Service
public class RestaurantApprovalRequestMessageListenerImpl implements RestaurantApprovalRequestMessageListener {
    private final RestaurantApprovalRequestHelper restaurantApprovalRequestHelper;
    private final OrderApprovedMessagePublisher orderApprovedMessagePublisher;
    private final OrderRejectedMessagePublisher orderRejectedMessagePublisher;

    public RestaurantApprovalRequestMessageListenerImpl(RestaurantApprovalRequestHelper restaurantApprovalRequestHelper,
                                                        OrderApprovedMessagePublisher orderApprovedMessagePublisher,
                                                        OrderRejectedMessagePublisher orderRejectedMessagePublisher) {
        this.restaurantApprovalRequestHelper = restaurantApprovalRequestHelper;
        this.orderApprovedMessagePublisher = orderApprovedMessagePublisher;
        this.orderRejectedMessagePublisher = orderRejectedMessagePublisher;
    }

    @Override
    public void approveOrder(RestaurantApprovalRequest restaurantApprovalRequest) {
        OrderApprovalEvent orderApprovalEvent = restaurantApprovalRequestHelper.persistOrderApproval(restaurantApprovalRequest);
        switch (orderApprovalEvent.getOrderApproval().getApprovalStatus()) {
            case APPROVED -> publishApprovedOrder(orderApprovalEvent);
            case REJECTED -> publishRejectedOrder(orderApprovalEvent);
        }
    }

    private void publishApprovedOrder(OrderApprovalEvent orderApprovalEvent) {
        orderApprovedMessagePublisher.publish(
                new OrderApprovedEvent(
                        orderApprovalEvent.getOrderApproval(),
                        orderApprovalEvent.getRestaurantId(),
                        orderApprovalEvent.getCreatedAt())
        );
    }

    private void publishRejectedOrder(OrderApprovalEvent orderApprovalEvent) {
        orderRejectedMessagePublisher.publish(
                new OrderRejectedEvent(
                        orderApprovalEvent.getOrderApproval(),
                        orderApprovalEvent.getRestaurantId(),
                        orderApprovalEvent.getFailureMessages(),
                        orderApprovalEvent.getCreatedAt()
                )
        );
    }
}
