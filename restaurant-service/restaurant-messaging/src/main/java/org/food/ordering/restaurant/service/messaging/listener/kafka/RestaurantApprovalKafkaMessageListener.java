package org.food.ordering.restaurant.service.messaging.listener.kafka;

import org.food.ordering.kafka.consumer.KafkaConsumer;
import org.food.ordering.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import org.food.ordering.restaurant.service.domain.port.input.message.listener.RestaurantApprovalRequestMessageListener;
import org.food.ordering.restaurant.service.messaging.mapper.RestaurantMessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RestaurantApprovalKafkaMessageListener implements KafkaConsumer<RestaurantApprovalRequestAvroModel> {
    private static final Logger log = LoggerFactory.getLogger(RestaurantApprovalKafkaMessageListener.class);

    private final RestaurantApprovalRequestMessageListener restaurantApprovalRequestMessageListener;
    private final RestaurantMessageMapper restaurantMessageMapper;

    public RestaurantApprovalKafkaMessageListener(RestaurantApprovalRequestMessageListener restaurantApprovalRequestMessageListener, RestaurantMessageMapper restaurantMessageMapper) {
        this.restaurantApprovalRequestMessageListener = restaurantApprovalRequestMessageListener;
        this.restaurantMessageMapper = restaurantMessageMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
                    topics = "${restaurant-service.restaurant-approval-request-topic-name}")
    public void receive(
            @Payload List<RestaurantApprovalRequestAvroModel> messages,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
            @Header(KafkaHeaders.OFFSET) List<Long> offsets
    ) {
        log.info("{} number of orders approval requests received with keys {}, partitions {}, and offsets {}, sending for restaurant approval",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(this::callAccurateListener);
    }

    private void callAccurateListener(RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel) {
        log.info("Processing order approval for order id: {}", restaurantApprovalRequestAvroModel.getOrderId());
        switch(restaurantApprovalRequestAvroModel.getRestaurantOrderStatus()) {
            case PAID -> callPaidListener(restaurantApprovalRequestAvroModel);
        }
    }

    private void callPaidListener(RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel) {
        restaurantApprovalRequestMessageListener.approveOrder(
                restaurantMessageMapper.restaurantApprovalRequestFromRestaurantApprovalRequestAvroModel(restaurantApprovalRequestAvroModel)
        );
    }
}
