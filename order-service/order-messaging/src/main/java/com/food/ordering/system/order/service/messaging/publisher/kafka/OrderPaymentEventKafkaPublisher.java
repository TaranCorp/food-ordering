package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.food.ordering.system.ordering.outbox.OutboxStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.function.BiConsumer;

@Component
public class OrderPaymentEventKafkaPublisher implements PaymentRequestMessagePublisher {
    private static final Logger log = LoggerFactory.getLogger(OrderPaymentEventKafkaPublisher.class);

    private final KafkaMessageHelper kafkaMessageHelper;
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    private final OrderServiceConfigData orderServiceConfigData;

    public OrderPaymentEventKafkaPublisher(KafkaMessageHelper kafkaMessageHelper,
                                           OrderMessagingDataMapper orderMessagingDataMapper,
                                           KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer,
                                           OrderServiceConfigData orderServiceConfigData) {
        this.kafkaMessageHelper = kafkaMessageHelper;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.orderServiceConfigData = orderServiceConfigData;
    }


    @Override
    public void publish(
            OrderPaymentOutboxMessage orderPaymentOutboxMessage,
            BiConsumer<OrderPaymentOutboxMessage, OutboxStatus> outboxCallback
    ) {
        final OrderPaymentEventPayload orderPaymentEventPayload = kafkaMessageHelper.getEventPayload(orderPaymentOutboxMessage.getPayload(), OrderPaymentEventPayload.class);
        final String sagaId = orderPaymentOutboxMessage.getSagaId().toString();
        final String orderId = orderPaymentEventPayload.getOrderId();

        log.info("Received OrderPaymentOutboxMessage for order id: {}, with saga id: {}", orderId, sagaId);

        final PaymentRequestAvroModel requestAvroModel = orderMessagingDataMapper.orderPaymentEventPayloadToPaymentRequestAvroModel(orderPaymentEventPayload, sagaId);

        try {
            kafkaProducer.send(
                    orderServiceConfigData.getPaymentRequestTopicName(),
                    sagaId,
                    requestAvroModel,
                    kafkaMessageHelper.getKafkaCallback(
                            orderServiceConfigData.getPaymentRequestTopicName(),
                            requestAvroModel,
                            orderId,
                            "PaymentRequestAvroModel",
                            orderPaymentOutboxMessage,
                            outboxCallback));

            log.info("OrderPaymentEventPayload sent to kafka for order id: {}, with saga id: {}", orderId, sagaId);
        } catch (Exception e) {
            log.error("Error while sending OrderPaymentEventPayload to kafka for order id: {}, with saga id: {}, error: {}"
                    , orderId, sagaId, e.getMessage());
        }
    }
}
