package org.food.ordering.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.food.ordering.domain.event.OrderPaidEvent;
import org.food.ordering.order.service.domain.dto.message.PaymentResponse;
import org.food.ordering.order.service.domain.port.input.message.listener.payment.PaymentResponseMessageListener;
import org.food.ordering.order.service.domain.port.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Slf4j
public class PaymentResponseMessageListenerImpl implements PaymentResponseMessageListener {

    private final OrderPaymentSaga orderPaymentSaga;
    private final OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher;

    public PaymentResponseMessageListenerImpl(OrderPaymentSaga orderPaymentSaga,
                                              OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher) {
        this.orderPaymentSaga = orderPaymentSaga;
        this.orderPaidRestaurantRequestMessagePublisher = orderPaidRestaurantRequestMessagePublisher;
    }

    @Override
    public void paymentCompleted(PaymentResponse paymentResponse) {
        OrderPaidEvent event = orderPaymentSaga.process(paymentResponse);
        log.info("Publishing OrderPaidEvent for order id: {}", paymentResponse.getOrderId());
        orderPaidRestaurantRequestMessagePublisher.publish(event);
    }

    @Override
    public void paymentCancelled(PaymentResponse paymentResponse) {
        orderPaymentSaga.rollback(paymentResponse);
        log.info("Order is rolling back for order id: {}, with failure messages: {}",
                paymentResponse.getOrderId(),
                String.join(", ", paymentResponse.getFailureMessages()));
    }
}
