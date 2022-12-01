package org.food.ordering.payment.service.domain;

import org.food.ordering.payment.service.domain.entity.CreditEntry;
import org.food.ordering.payment.service.domain.entity.CreditHistory;
import org.food.ordering.payment.service.domain.entity.Payment;
import org.food.ordering.payment.service.domain.event.PaymentEvent;

import java.util.List;

public interface PaymentDomainService {
    PaymentEvent validateAndInitiatePayment(
            Payment payment,
            CreditEntry creditEntry,
            List<CreditHistory> creditHistory,
            List<String> failureMessages
    );

    PaymentEvent validateAndCancelPayment(
            Payment payment,
            CreditEntry creditEntry,
            List<CreditHistory> creditHistory,
            List<String> failureMessages
    );
}
