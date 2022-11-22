package org.food.ordering.order.service.domain.dto.track;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class TrackOrderQuery {

    @NotNull
    private final UUID orderTrackingId;

    public TrackOrderQuery(UUID orderTrackingId) {
        this.orderTrackingId = orderTrackingId;
    }

    public UUID getOrderTrackingId() {
        return orderTrackingId;
    }
}
