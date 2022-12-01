package org.food.ordering.payment.service.domain.event;

import org.food.ordering.payment.service.domain.entity.Payment;

import java.util.List;

public class PaymentFailedEvent extends PaymentEvent {
    public PaymentFailedEvent(Payment payment, List<String> failureMessages) {
        super(payment, failureMessages);
    }
}
