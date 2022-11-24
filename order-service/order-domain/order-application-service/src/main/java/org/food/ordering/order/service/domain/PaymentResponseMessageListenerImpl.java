package org.food.ordering.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.food.ordering.order.service.domain.dto.message.PaymentResponse;
import org.food.ordering.order.service.domain.port.input.message.listener.payment.PaymentResponseMessageListener;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Slf4j
public class PaymentResponseMessageListenerImpl implements PaymentResponseMessageListener {

    @Override
    public void paymentCompleted(PaymentResponse paymentResponse) {

    }

    @Override
    public void paymentCancelled(PaymentResponse paymentResponse) {

    }
}
