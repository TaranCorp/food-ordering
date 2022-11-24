package org.food.ordering.order.service.domain.dto.track;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class TrackOrderQuery {

    @NotNull
    private final UUID orderTrackingId;

    private TrackOrderQuery(UUID orderTrackingId) {
        this.orderTrackingId = orderTrackingId;
    }

    public static TrackOrderQuery createTrackerBy(UUID trackingId) {
        return new TrackOrderQuery(trackingId);
    }

    public UUID getOrderTrackingId() {
        return orderTrackingId;
    }
}
