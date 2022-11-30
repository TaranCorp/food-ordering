package org.food.ordering.order.service.messaging.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.food.ordering.kafka.consumer.KafkaConsumer;
import org.food.ordering.kafka.order.avro.model.PaymentResponseAvroModel;
import org.food.ordering.order.service.domain.port.input.message.listener.payment.PaymentResponseMessageListener;
import org.food.ordering.order.service.messaging.kafka.mapper.OrderMessagingDataMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
class PaymentResponseKafkaListener implements KafkaConsumer<PaymentResponseAvroModel> {

    private final PaymentResponseMessageListener listener;
    private final OrderMessagingDataMapper mapper;

    PaymentResponseKafkaListener(PaymentResponseMessageListener listener, OrderMessagingDataMapper mapper) {
        this.listener = listener;
        this.mapper = mapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}", topics = "${order-service.payment-response-topic-name}")
    public void receive(
            @Payload List<PaymentResponseAvroModel> messages,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
            @Header(KafkaHeaders.OFFSET) List<Long> offsets
    ) {
        log.info("{} number of payment responses with keys: {}, partitions: {}, offsets: {}", messages.size(), keys, partitions, offsets);
        messages.forEach(this::callAccurateListener);
    }

    private void callAccurateListener(PaymentResponseAvroModel response) {
        switch(response.getPaymentStatus()) {
            case COMPLETED -> listenForCompletedPayment(response);
            case CANCELLED, FAILED -> listenForCancelledPayment(response);
        }
    }

    private void listenForCancelledPayment(PaymentResponseAvroModel response) {
        log.info("Processing unsuccessful payment for: {}", response.getOrderId());
        listener.paymentCancelled(mapper.paymentResponseFromPaymentResponseAvroModel(response));
    }

    private void listenForCompletedPayment(PaymentResponseAvroModel response) {
        log.info("Processing successful payment for: {}", response.getOrderId());
        listener.paymentCompleted(mapper.paymentResponseFromPaymentResponseAvroModel(response));
    }
}
