package org.food.ordering.domain.exception;

public class OrderItemException extends DomainException {
    public OrderItemException(String message) {
        super(message);
    }

    public OrderItemException(String message, Throwable cause) {
        super(message, cause);
    }
}
