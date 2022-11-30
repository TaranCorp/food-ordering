package org.food.ordering.order.service.dataaccess.restaurant.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
