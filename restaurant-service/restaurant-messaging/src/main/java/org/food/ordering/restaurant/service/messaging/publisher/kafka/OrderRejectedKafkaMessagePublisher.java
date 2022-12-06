package org.food.ordering.restaurant.service.messaging.publisher.kafka;

import org.food.ordering.domain.valueobject.OrderId;
import org.food.ordering.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import org.food.ordering.kafka.producer.KafkaPublisherCommons;
import org.food.ordering.kafka.producer.service.KafkaProducer;
import org.food.ordering.restaurant.service.domain.config.RestaurantServiceConfig;
import org.food.ordering.restaurant.service.domain.event.OrderRejectedEvent;
import org.food.ordering.restaurant.service.domain.port.output.message.publisher.OrderRejectedMessagePublisher;
import org.food.ordering.restaurant.service.messaging.mapper.RestaurantMessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderRejectedKafkaMessagePublisher implements OrderRejectedMessagePublisher {
    private static final Logger log = LoggerFactory.getLogger(OrderRejectedKafkaMessagePublisher.class);

    private final RestaurantMessageMapper restaurantMessageMapper;
    private final KafkaProducer<String, RestaurantApprovalResponseAvroModel> kafkaProducer;
    private final RestaurantServiceConfig restaurantServiceConfig;
    private final KafkaPublisherCommons kafkaPublisherCommons;

    public OrderRejectedKafkaMessagePublisher(RestaurantMessageMapper restaurantMessageMapper,
                                              KafkaProducer<String, RestaurantApprovalResponseAvroModel> kafkaProducer,
                                              RestaurantServiceConfig restaurantServiceConfig,
                                              KafkaPublisherCommons kafkaPublisherCommons) {
        this.restaurantMessageMapper = restaurantMessageMapper;
        this.kafkaProducer = kafkaProducer;
        this.restaurantServiceConfig = restaurantServiceConfig;
        this.kafkaPublisherCommons = kafkaPublisherCommons;
    }

    @Override
    public void publish(OrderRejectedEvent domainEvent) {
        final OrderId orderId = domainEvent.getOrderApproval().getOrderId();

        log.info("Received OrderRejectedEvent for order id: {}", orderId);

        RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModel = restaurantMessageMapper.restaurantApprovalResponseAvroModelFromOrderRejectedEvent(domainEvent);

        try {
            kafkaProducer.send(
                    restaurantServiceConfig.getRestaurantApprovalResponseTopicName(),
                    orderId.toString(),
                    restaurantApprovalResponseAvroModel,
                    kafkaPublisherCommons.getKafkaCallback(
                            restaurantServiceConfig.getRestaurantApprovalResponseTopicName(),
                            restaurantApprovalResponseAvroModel,
                            orderId.toString(),
                            "RestaurantApprovalResponseAvroModel"
                    )
            );

            log.info("Sent RestaurantApprovalResponseAvroModel for order id: {}, at: {}", orderId, System.nanoTime());
        } catch (Exception e) {
            log.error("Error while sending RestaurantApprovalResponseAvroModel for order id: {}, with message: {}", orderId, e.getMessage());
        }
    }
}
