package org.food.ordering.order.service.domain.valueobject;

import org.food.ordering.domain.valueobject.BaseId;

public class OrderItemId extends BaseId<Long> {
    public OrderItemId(Long value) {
        super(value);
    }
}
