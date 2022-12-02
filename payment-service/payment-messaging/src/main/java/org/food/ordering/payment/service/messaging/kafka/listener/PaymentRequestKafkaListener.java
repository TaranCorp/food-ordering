package org.food.ordering.payment.service.messaging.kafka.listener;

import org.food.ordering.kafka.consumer.KafkaConsumer;
import org.food.ordering.kafka.order.avro.model.PaymentRequestAvroModel;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentRequestKafkaListener implements KafkaConsumer<PaymentRequestAvroModel> {
    @Override
    public void receive(List<PaymentRequestAvroModel> messages, List<String> keys, List<Integer> partitions, List<Long> offsets) {

    }
}
