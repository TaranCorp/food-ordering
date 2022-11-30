package org.food.ordering.order.service.messaging.kafka.publisher;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.Instant;

@Component
@Slf4j
class KafkaPublisherCommons {
    <T> ListenableFutureCallback<SendResult<String, T>> getKafkaCallback(String responseTopicName, T requestAvroModel, String orderId, String requestAvroModelName) {
        return new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Error while sending "+ requestAvroModelName +" message: {} to topic: {}", requestAvroModel.toString(), responseTopicName);
            }

            @Override
            public void onSuccess(SendResult<String, T> result) {
                RecordMetadata recordMetadata = result.getRecordMetadata();
                log.error("Received successful response from Kafka for order id: {} Topic: {} Partition: {} Offset: {} Timestamp: {}",
                        orderId, responseTopicName, recordMetadata.partition(), recordMetadata.offset(), Instant.ofEpochMilli(recordMetadata.timestamp()));
            }
        };
    }
}
