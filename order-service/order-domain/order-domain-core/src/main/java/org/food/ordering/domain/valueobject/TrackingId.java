package org.food.ordering.domain.valueobject;

import java.util.UUID;

public class TrackingId extends BaseId<UUID> {
    public static TrackingId of(UUID id) {
        return new TrackingId(id);
    }

    public TrackingId(UUID value) {
        super(value);
    }
}
