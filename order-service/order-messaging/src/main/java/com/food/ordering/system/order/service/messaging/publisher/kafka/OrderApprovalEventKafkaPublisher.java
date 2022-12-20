package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.food.ordering.system.ordering.outbox.OutboxStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
public class OrderApprovalEventKafkaPublisher implements RestaurantApprovalRequestMessagePublisher {
    private static final Logger log = LoggerFactory.getLogger(OrderApprovalEventKafkaPublisher.class);

    private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;
    private final OrderServiceConfigData orderServiceConfigData;
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final KafkaMessageHelper kafkaMessageHelper;

    public OrderApprovalEventKafkaPublisher(KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer,
                                            OrderServiceConfigData orderServiceConfigData,
                                            OrderMessagingDataMapper orderMessagingDataMapper,
                                            KafkaMessageHelper kafkaMessageHelper) {
        this.kafkaProducer = kafkaProducer;
        this.orderServiceConfigData = orderServiceConfigData;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(
            OrderApprovalOutboxMessage orderApprovalOutboxMessage,
            BiConsumer<OrderApprovalOutboxMessage, OutboxStatus> outboxCallback
    ) {
        final OrderApprovalEventPayload orderApprovalEventPayload = kafkaMessageHelper.getEventPayload(orderApprovalOutboxMessage.getPayload(), OrderApprovalEventPayload.class);
        final String topicName = orderServiceConfigData.getRestaurantApprovalRequestTopicName();
        final String sagaId = orderApprovalOutboxMessage.getSagaId().toString();
        final String orderId = orderApprovalEventPayload.getOrderId();

        log.info("Received OrderApprovalOutboxMessage for order id: {}, with saga id: {}", orderId, sagaId);

        final RestaurantApprovalRequestAvroModel requestAvroModel = orderMessagingDataMapper.restaurantApprovalRequestAvroModelFromOrderApprovalEventPayload(sagaId, orderApprovalEventPayload);


        try {
            kafkaProducer.send(
                    topicName,
                    sagaId,
                    requestAvroModel,
                    kafkaMessageHelper.getKafkaCallback(
                            topicName,
                            requestAvroModel,
                            orderId,
                            "RestaurantApprovalRequestAvroModel",
                            orderApprovalOutboxMessage,
                            outboxCallback
                    )
            );
            log.info("Successfully sent RestaurantApprovalRequestAvroModel to kafka for order id: {}, with saga id: {}", orderId, sagaId);
        } catch (Exception e) {
            log.error("There was an error while publishing RestaurantApprovalRequestAvroModel for order id: {}, with saga id: {}", orderId, sagaId);
        }
    }
}
