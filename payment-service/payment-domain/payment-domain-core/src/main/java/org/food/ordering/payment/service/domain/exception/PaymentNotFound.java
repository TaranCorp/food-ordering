package org.food.ordering.payment.service.domain.exception;

import org.food.ordering.domain.exception.DomainException;

public class PaymentNotFound extends DomainException {
    public PaymentNotFound(String message) {
        super(message);
    }

    public PaymentNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
