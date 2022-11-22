package org.food.ordering.domain.exception;

public class BadArgumentException extends RuntimeException {
    public BadArgumentException() {
        super();
    }

    public BadArgumentException(String message) {
        super(message);
    }
}
