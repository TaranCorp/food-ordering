package org.food.ordering.payment.service.messaging.kafka.listener;

import org.food.ordering.kafka.consumer.KafkaConsumer;
import org.food.ordering.kafka.order.avro.model.PaymentRequestAvroModel;
import org.food.ordering.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import org.food.ordering.payment.service.messaging.kafka.mapper.PaymentMessagingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentRequestKafkaListener implements KafkaConsumer<PaymentRequestAvroModel> {
    private static final Logger log = LoggerFactory.getLogger(PaymentRequestKafkaListener.class);

    private final PaymentRequestMessageListener paymentRequestMessageListener;
    private final PaymentMessagingMapper paymentMessagingMapper;

    public PaymentRequestKafkaListener(PaymentRequestMessageListener paymentRequestMessageListener, PaymentMessagingMapper paymentMessagingMapper) {
        this.paymentRequestMessageListener = paymentRequestMessageListener;
        this.paymentMessagingMapper = paymentMessagingMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}", topics = "${payment-service.payment-request-topic-name}")
    public void receive(
            @Payload List<PaymentRequestAvroModel> messages,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
            @Header(KafkaHeaders.OFFSET) List<Long> offsets
    ) {
        log.info("{} number of payment requests received with keys: {}, partitions: {} and offsets: {}",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString());
        messages.forEach(this::callAccurateListener);
    }

    private void callAccurateListener(PaymentRequestAvroModel paymentRequestAvroModel) {
        switch (paymentRequestAvroModel.getPaymentOrderStatus()) {
            case PENDING -> callPendingListener(paymentRequestAvroModel);
            case CANCELLED -> callCancelledListener(paymentRequestAvroModel);
        }
    }

    private void callPendingListener(PaymentRequestAvroModel paymentRequestAvroModel) {
        log.info("Calling CompletePaymentListener for order id: {}", paymentRequestAvroModel.getOrderId());
        paymentRequestMessageListener.completePayment(
                paymentMessagingMapper.paymentRequestFromPaymentRequestAvroModel(paymentRequestAvroModel)
        );
    }

    private void callCancelledListener(PaymentRequestAvroModel paymentRequestAvroModel) {
        log.info("Calling CancelledPaymentListener for order id: {}", paymentRequestAvroModel.getOrderId());
        paymentRequestMessageListener.cancelPayment(
                paymentMessagingMapper.paymentRequestFromPaymentRequestAvroModel(paymentRequestAvroModel)
        );
    }
}
