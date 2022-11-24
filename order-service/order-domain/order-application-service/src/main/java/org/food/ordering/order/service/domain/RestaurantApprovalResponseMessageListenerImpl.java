package org.food.ordering.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.food.ordering.order.service.domain.dto.message.RestaurantApprovalResponse;
import org.food.ordering.order.service.domain.port.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Slf4j
public class RestaurantApprovalResponseMessageListenerImpl implements RestaurantApprovalResponseMessageListener {
    @Override
    public void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse) {

    }

    @Override
    public void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse) {

    }
}
