package org.food.ordering.order.service.domain.util;

import org.food.ordering.domain.event.OrderEvent;
import org.food.ordering.domain.exception.BadArgumentException;
import org.food.ordering.domain.valueobject.BaseId;

import java.time.Instant;
import java.time.ZonedDateTime;

public final class DomainUtils {
    private DomainUtils() {
    }

    public static <T extends OrderEvent> Instant extractInstant(T orderEvent) {
        ZonedDateTime createdAt = orderEvent.getCreatedAt();
        if (createdAt == null) {
            throw new BadArgumentException("Cannot process null timestamp");
        }
        return createdAt.toInstant();
    }

    public static <T extends BaseId> String extractId(T toPull) {
        Object uuid = toPull.getValue();
        if (uuid == null) {
            throw new BadArgumentException("Cannot process null UUID");
        }
        return uuid.toString();
    }
}
