package org.food.ordering.payment.service.domain;

import org.food.ordering.domain.valueobject.PaymentStatus;
import org.food.ordering.payment.service.domain.dto.PaymentRequest;
import org.food.ordering.payment.service.domain.event.PaymentCancelledEvent;
import org.food.ordering.payment.service.domain.event.PaymentCompletedEvent;
import org.food.ordering.payment.service.domain.event.PaymentEvent;
import org.food.ordering.payment.service.domain.event.PaymentFailedEvent;
import org.food.ordering.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import org.food.ordering.payment.service.domain.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import org.food.ordering.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import org.food.ordering.payment.service.domain.ports.output.message.publisher.PaymentFailedMessagePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PaymentRequestMessageListenerImpl implements PaymentRequestMessageListener {
    private static final Logger log = LoggerFactory.getLogger(PaymentRequestMessageListenerImpl.class);

    private final PaymentRequestHelper paymentRequestHelper;
    private final PaymentFailedMessagePublisher paymentFailedMessagePublisher;
    private final PaymentCancelledMessagePublisher paymentCancelledMessagePublisher;
    private final PaymentCompletedMessagePublisher paymentCompletedMessagePublisher;

    public PaymentRequestMessageListenerImpl(PaymentRequestHelper paymentRequestHelper,
                                             PaymentFailedMessagePublisher paymentFailedMessagePublisher,
                                             PaymentCancelledMessagePublisher paymentCancelledMessagePublisher,
                                             PaymentCompletedMessagePublisher paymentCompletedMessagePublisher) {
        this.paymentRequestHelper = paymentRequestHelper;
        this.paymentFailedMessagePublisher = paymentFailedMessagePublisher;
        this.paymentCancelledMessagePublisher = paymentCancelledMessagePublisher;
        this.paymentCompletedMessagePublisher = paymentCompletedMessagePublisher;
    }

    @Override
    public void completePayment(PaymentRequest paymentRequest) {
        PaymentEvent paymentEvent = paymentRequestHelper.persistPayment(paymentRequest);
        if (paymentEvent.getPayment().getPaymentStatus() == PaymentStatus.COMPLETED) {
            paymentCompletedMessagePublisher.publish(new PaymentCompletedEvent(paymentEvent.getPayment()));
        }
        paymentFailedMessagePublisher.publish(new PaymentFailedEvent(paymentEvent.getPayment(), paymentEvent.getFailureMessages()));
    }

    @Override
    public void cancelPayment(PaymentRequest paymentRequest) {
        PaymentEvent paymentEvent = paymentRequestHelper.persistCancelPayment(paymentRequest);
        if (paymentEvent.getPayment().getPaymentStatus() == PaymentStatus.CANCELLED) {
            paymentCancelledMessagePublisher.publish(new PaymentCancelledEvent(paymentEvent.getPayment()));
        }
        paymentFailedMessagePublisher.publish(new PaymentFailedEvent(paymentEvent.getPayment(), paymentEvent.getFailureMessages()));
    }
}
