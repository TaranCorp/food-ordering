package org.food.ordering.payment.service.domain.event;

import org.food.ordering.payment.service.domain.entity.Payment;

import java.util.Collections;

public class PaymentCompletedEvent extends PaymentEvent {
    public PaymentCompletedEvent(Payment payment) {
        super(payment, Collections.emptyList());
    }
}
