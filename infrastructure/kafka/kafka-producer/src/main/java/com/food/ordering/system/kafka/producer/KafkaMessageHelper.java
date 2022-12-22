package com.food.ordering.system.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.ordering.outbox.OutboxStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class KafkaMessageHelper {

    private final ObjectMapper objectMapper;

    public KafkaMessageHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T, T2> ListenableFutureCallback<SendResult<String, T>>
    getKafkaCallback(
            String responseTopicName,
            T avroModel,
            String orderId,
            String avroModelName,
            T2 outboxMessage,
            BiConsumer<T2, OutboxStatus> outboxCallback
    ) {
        return new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Error while sending " + avroModelName +
                        " message {} to topic {}", avroModel.toString(), responseTopicName, ex);
                outboxCallback.accept(outboxMessage, OutboxStatus.FAILED);
            }

            @Override
            public void onSuccess(SendResult<String, T> result) {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Received successful response from Kafka for order id: {}" +
                                " Topic: {} Partition: {} Offset: {} Timestamp: {}",
                        orderId,
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp()
                );
                outboxCallback.accept(outboxMessage, OutboxStatus.COMPLETED);
            }
        };
    }

    public <T> T getEventPayload(String payload, Class<T> type) {
        try {
            return objectMapper.readValue(payload, type);
        } catch (JsonProcessingException e) {
            final String errorMsg = "There was an error while parsing %s".formatted(type.getSimpleName());
            log.error(errorMsg);
            throw new OrderDomainException(errorMsg, e);
        } catch (Exception e) {
            log.error("WRONG LONG", e);
            throw new OrderDomainException(e.getMessage());
        }
    }
}
