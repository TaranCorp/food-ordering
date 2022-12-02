package org.food.ordering.kafka.producer;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.Instant;

@Component
public class KafkaPublisherCommons {
    private static final Logger log = LoggerFactory.getLogger(KafkaPublisherCommons.class);

    public <T> ListenableFutureCallback<SendResult<String, T>> getKafkaCallback(
            String topicName,
            T avroModel,
            String orderId,
            String avroModelName
    ) {
        return new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Error while sending {} message: {} to topic: {}",
                        avroModelName, avroModel.toString(), topicName);
            }

            @Override
            public void onSuccess(SendResult<String, T> result) {
                RecordMetadata recordMetadata = result.getRecordMetadata();
                log.error("Received successful response from Kafka for order" +
                                " id: {}" +
                                " Topic: {}" +
                                " Partition: {}" +
                                " Offset: {}" +
                                " Timestamp: {}",
                                orderId,
                                topicName,
                                recordMetadata.partition(),
                                recordMetadata.offset(),
                                Instant.ofEpochMilli(recordMetadata.timestamp()));
            }
        };
    }
}
