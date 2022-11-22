package org.food.ordering.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.food.ordering.order.service.domain.dto.track.TrackOrderQuery;
import org.food.ordering.order.service.domain.dto.track.TrackOrderResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class OrderTrackCommandHandler {
    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
        return null;
    }
}
