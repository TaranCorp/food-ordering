package org.food.ordering.order.service.messaging.kafka.publisher;

import lombok.extern.slf4j.Slf4j;
import org.food.ordering.domain.entity.Order;
import org.food.ordering.domain.event.OrderPaidEvent;
import org.food.ordering.domain.exception.OrderDomainException;
import org.food.ordering.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import org.food.ordering.kafka.producer.service.KafkaProducer;
import org.food.ordering.order.service.domain.config.OrderServiceConfigData;
import org.food.ordering.order.service.domain.port.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import org.food.ordering.order.service.messaging.kafka.mapper.OrderMessagingDataMapper;
import org.springframework.stereotype.Component;

import static org.food.ordering.order.service.domain.util.DomainUtils.extractId;

@Component
@Slf4j
public class PayOrderKafkaMessagePublisher implements OrderPaidRestaurantRequestMessagePublisher {

    private final OrderMessagingDataMapper mapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;
    private final KafkaPublisherCommons kafkaPublisherCommons;

    public PayOrderKafkaMessagePublisher(OrderMessagingDataMapper mapper,
                                         OrderServiceConfigData orderServiceConfigData,
                                         KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer,
                                         KafkaPublisherCommons kafkaPublisherCommons) {
        this.mapper = mapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
        this.kafkaPublisherCommons = kafkaPublisherCommons;
    }

    @Override
    public void publish(OrderPaidEvent domainEvent) {
        Order order = domainEvent.getOrder();
        if (order == null) {
            throw new OrderDomainException("Cannot process null domain event");
        }

        String orderId = extractId(order.getId());
        log.info("Received OrderPaidEvent for order id: {}", orderId);

        try {
            RestaurantApprovalRequestAvroModel avroModel  = mapper.restaurantApprovalRequestAvroModelFromOrderPaidEvent(domainEvent);
            kafkaProducer.send(
                    orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
                    orderId,
                    avroModel,
                    kafkaPublisherCommons.getKafkaCallback(
                            orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
                            avroModel,
                            orderId,
                            "RestaurantApprovalRequestAvroModel"
                    )
            );
            log.info("RestaurantApprovalRequestAvroModel sent to kafka for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while sending RestaurantApprovalRequestAvroModel message to kafka with order id: {}, error: {}", orderId, e.getMessage());
        }
    }
}
