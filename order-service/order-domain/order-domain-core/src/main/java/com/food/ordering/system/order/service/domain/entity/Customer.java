package org.food.ordering.order.service.domain.entity;

import org.food.ordering.domain.entity.AggregateRoot;
import org.food.ordering.domain.valueobject.CustomerId;

public class Customer extends AggregateRoot<CustomerId> {
    public Customer() {
    }

    public Customer(CustomerId customerId) {
        super.setId(customerId);
    }
}
