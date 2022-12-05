package org.food.ordering.restaurant.service.domain.exception;

import org.food.ordering.domain.exception.DomainException;

public class RestaurantNotFoundException extends DomainException {
    public RestaurantNotFoundException(String msg) {
        super(msg);
    }
}
