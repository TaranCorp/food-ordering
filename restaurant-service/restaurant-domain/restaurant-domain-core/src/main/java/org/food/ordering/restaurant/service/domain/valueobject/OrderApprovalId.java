package org.food.ordering.restaurant.service.domain.valueobject;

import org.food.ordering.domain.valueobject.BaseId;

import java.util.UUID;

public class OrderApprovalId extends BaseId<UUID> {
    public OrderApprovalId(UUID value) {
        super(value);
    }
}
