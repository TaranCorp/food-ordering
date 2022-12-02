package org.food.ordering.order.service.messaging.kafka.publisher;

import lombok.extern.slf4j.Slf4j;
import org.food.ordering.domain.entity.Order;
import org.food.ordering.domain.event.OrderCreatedEvent;
import org.food.ordering.domain.exception.OrderDomainException;
import org.food.ordering.kafka.order.avro.model.PaymentRequestAvroModel;
import org.food.ordering.kafka.producer.KafkaPublisherCommons;
import org.food.ordering.kafka.producer.service.KafkaProducer;
import org.food.ordering.order.service.domain.config.OrderServiceConfigData;
import org.food.ordering.order.service.domain.port.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import org.food.ordering.order.service.messaging.kafka.mapper.OrderMessagingDataMapper;
import org.springframework.stereotype.Component;

import static org.food.ordering.order.service.domain.util.DomainUtils.extractId;

@Slf4j
@Component
public class CreateOrderKafkaMessagePublisher implements OrderCreatedPaymentRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    private final KafkaPublisherCommons kafkaPublisherCommons;

    public CreateOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                            OrderServiceConfigData orderServiceConfigData,
                                            KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer,
                                            KafkaPublisherCommons kafkaPublisherCommons) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
        this.kafkaPublisherCommons = kafkaPublisherCommons;
    }

    @Override
    public void publish(OrderCreatedEvent domainEvent) {
        Order order = domainEvent.getOrder();
        if (order == null) {
            throw new OrderDomainException("Cannot process null domain event");
        }

        String orderId = extractId(order.getId());
        log.info("Received OrderCreatedEvent for order id: {}", orderId);

        try {
            PaymentRequestAvroModel paymentRequestAvroModel = orderMessagingDataMapper.paymentRequestAvroModelFromOrderCreatedEvent(domainEvent);

            kafkaProducer.send(
                    orderServiceConfigData.getPaymentRequestTopicName(),
                    orderId,
                    paymentRequestAvroModel,
                    kafkaPublisherCommons.getKafkaCallback(
                            orderServiceConfigData.getPaymentResponseTopicName(),
                            paymentRequestAvroModel,
                            orderId,
                            "PaymentRequestAvroModel"
                    )
            );

            log.info("PaymentRequestAvroModel sent to Kafka for order id: {}", paymentRequestAvroModel.getOrderId());
        } catch (Exception e) {
            log.error("Error while sending PaymentRequestAvroModel message to kafka with order id: {}, error: {}", orderId, e.getMessage());
        }
    }
}
