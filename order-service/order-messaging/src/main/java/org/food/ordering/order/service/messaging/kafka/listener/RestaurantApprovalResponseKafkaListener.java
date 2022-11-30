package org.food.ordering.order.service.messaging.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.food.ordering.kafka.consumer.KafkaConsumer;
import org.food.ordering.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import org.food.ordering.order.service.domain.port.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import org.food.ordering.order.service.messaging.kafka.mapper.OrderMessagingDataMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
class RestaurantApprovalResponseKafkaListener implements KafkaConsumer<RestaurantApprovalResponseAvroModel> {

    private final RestaurantApprovalResponseMessageListener listener;
    private final OrderMessagingDataMapper mapper;

    RestaurantApprovalResponseKafkaListener(RestaurantApprovalResponseMessageListener listener, OrderMessagingDataMapper mapper) {
        this.listener = listener;
        this.mapper = mapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
                    topics = "${order-service.restaurant-approval-response-topic-name}")
    public void receive(
            @Payload List<RestaurantApprovalResponseAvroModel> messages,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
            @Header(KafkaHeaders.OFFSET) List<Long> offsets
    ) {
        log.info("Number of restaurant approval responses: {} received with keys {}, partitions {} and offsets {}", messages.size(), keys, partitions, offsets);
        messages.forEach(this::callAccurateListener);
    }

    private void callAccurateListener(RestaurantApprovalResponseAvroModel restaurantResponse) {
        switch(restaurantResponse.getOrderApprovalStatus()) {
            case APPROVED -> listenForRestaurantApprovalResponse(restaurantResponse);
            case REJECTED -> listenForRestaurantRejectedResponse(restaurantResponse);
        }
    }

    private void listenForRestaurantApprovalResponse(RestaurantApprovalResponseAvroModel restaurantResponse) {
        log.info("Processing approved restaurant response for order id: {}", restaurantResponse.getOrderId());
        listener.orderApproved(mapper.restaurantApprovalResponseFromRestaurantApprovalResponseAvroModel(restaurantResponse));
    }

    private void listenForRestaurantRejectedResponse(RestaurantApprovalResponseAvroModel restaurantResponse) {
        log.info("Processing rejected restaurant response for order id: {}, with failure message: {}", restaurantResponse.getOrderId(), restaurantResponse.getFailureMessages());
        listener.orderRejected(mapper.restaurantApprovalResponseFromRestaurantApprovalResponseAvroModel(restaurantResponse));
    }
}
