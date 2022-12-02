package org.food.ordering.payment.service.domain.mapper;

import org.food.ordering.domain.valueobject.CustomerId;
import org.food.ordering.domain.valueobject.Money;
import org.food.ordering.domain.valueobject.OrderId;
import org.food.ordering.payment.service.domain.dto.PaymentRequest;
import org.food.ordering.payment.service.domain.entity.Payment;
import org.food.ordering.payment.service.domain.event.PaymentCompletedEvent;
import org.food.ordering.payment.service.domain.event.PaymentEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentDataMapper {

    public Payment createPaymentFromPaymentRequest(PaymentRequest paymentRequest) {
        return new Payment(
                new OrderId(UUID.fromString(paymentRequest.getOrderId())),
                new CustomerId(UUID.fromString(paymentRequest.getCustomerId())),
                new Money(paymentRequest.getPrice())
        );
    }

    public PaymentCompletedEvent createPaymentCompleteEventFromPaymentEvent(PaymentEvent paymentEvent) {
        return new PaymentCompletedEvent(
                paymentEvent.getPayment()
        );
    }
}
