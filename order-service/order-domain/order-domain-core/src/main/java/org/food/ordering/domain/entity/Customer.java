package org.food.ordering.domain.entity;

import org.food.ordering.domain.valueobject.CustomerId;

import java.util.UUID;

public class Customer extends AggregateRoot<CustomerId> {
    public static Customer createCustomerById(UUID id) {
        Customer customer = new Customer();
        customer.setId(new CustomerId(id));
        return customer;
    }
}
