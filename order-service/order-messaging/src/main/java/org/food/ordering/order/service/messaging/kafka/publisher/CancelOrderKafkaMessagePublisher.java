package org.food.ordering.order.service.messaging.kafka.publisher;

import lombok.extern.slf4j.Slf4j;
import org.food.ordering.domain.entity.Order;
import org.food.ordering.domain.event.OrderCancelledEvent;
import org.food.ordering.domain.exception.OrderDomainException;
import org.food.ordering.kafka.order.avro.model.PaymentRequestAvroModel;
import org.food.ordering.kafka.producer.service.KafkaProducer;
import org.food.ordering.order.service.domain.config.OrderServiceConfigData;
import org.food.ordering.order.service.domain.port.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import org.food.ordering.order.service.messaging.kafka.mapper.OrderMessagingDataMapper;
import org.springframework.stereotype.Component;

import static org.food.ordering.order.service.domain.util.DomainUtils.extractId;

@Slf4j
@Component
public class CancelOrderKafkaMessagePublisher implements OrderCancelledPaymentRequestMessagePublisher {

    private final OrderServiceConfigData orderServiceConfigData;
    private final OrderMessagingDataMapper mapper;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    private final KafkaPublisherCommons kafkaPublisherCommons;

    public CancelOrderKafkaMessagePublisher(OrderServiceConfigData orderServiceConfigData,
                                            OrderMessagingDataMapper mapper,
                                            KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer,
                                            KafkaPublisherCommons kafkaPublisherCommons) {
        this.orderServiceConfigData = orderServiceConfigData;
        this.mapper = mapper;
        this.kafkaProducer = kafkaProducer;
        this.kafkaPublisherCommons = kafkaPublisherCommons;
    }

    @Override
    public void publish(OrderCancelledEvent domainEvent) {
        Order order = domainEvent.getOrder();
        if (order == null) {
            throw new OrderDomainException("Cannot process null domain event");
        }

        String orderId = extractId(order.getId());
        log.info("Received OrderCancelledEvent for order id: {}", orderId);

        try {
            PaymentRequestAvroModel avroModel = mapper.paymentRequestAvroModelFromOrderCancelledEvent(domainEvent);

            kafkaProducer.send(
                    orderServiceConfigData.getPaymentRequestTopicName(),
                    orderId,
                    avroModel,
                    kafkaPublisherCommons.getKafkaCallback(
                            orderServiceConfigData.getPaymentResponseTopicName(),
                            avroModel,
                            orderId,
                            "PaymentRequestAvroModel"
                    )
            );

            log.info("PaymentRequestAvroModel sent to Kafka for order id: {}", avroModel.getOrderId());
        } catch (Exception e) {
            log.error("Error while sending PaymentRequestAvroModel message to kafka with order id: {}, error: {}", orderId, e.getMessage());
        }
    }
}
