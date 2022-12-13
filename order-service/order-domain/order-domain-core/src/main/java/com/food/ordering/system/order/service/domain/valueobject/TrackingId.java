package org.food.ordering.order.service.domain.valueobject;

import org.food.ordering.domain.valueobject.BaseId;

import java.util.UUID;

public class TrackingId extends BaseId<UUID> {
    public TrackingId(UUID value) {
        super(value);
    }
}
