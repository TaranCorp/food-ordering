package org.food.ordering.payment.service.messaging.kafka.publisher;

import org.food.ordering.kafka.order.avro.model.PaymentResponseAvroModel;
import org.food.ordering.kafka.producer.KafkaPublisherCommons;
import org.food.ordering.kafka.producer.service.KafkaProducer;
import org.food.ordering.payment.service.domain.config.PaymentServiceConfig;
import org.food.ordering.payment.service.domain.event.PaymentCancelledEvent;
import org.food.ordering.payment.service.domain.event.PaymentFailedEvent;
import org.food.ordering.payment.service.domain.exception.PaymentDomainException;
import org.food.ordering.payment.service.domain.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import org.food.ordering.payment.service.domain.ports.output.message.publisher.PaymentFailedMessagePublisher;
import org.food.ordering.payment.service.messaging.kafka.mapper.PaymentMessagingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PaymentFailedKafkaPublisher implements PaymentFailedMessagePublisher {
    private static final Logger log = LoggerFactory.getLogger(PaymentFailedKafkaPublisher.class);

    private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;
    private final PaymentServiceConfig paymentServiceConfigData;
    private final PaymentMessagingMapper paymentMessagingMapper;
    private final KafkaPublisherCommons kafkaPublisherCommons;

    public PaymentFailedKafkaPublisher(KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer,
                                       PaymentServiceConfig paymentServiceConfigData,
                                       PaymentMessagingMapper paymentMessagingMapper,
                                       KafkaPublisherCommons kafkaPublisherCommons) {
        this.kafkaProducer = kafkaProducer;
        this.paymentServiceConfigData = paymentServiceConfigData;
        this.paymentMessagingMapper = paymentMessagingMapper;
        this.kafkaPublisherCommons = kafkaPublisherCommons;
    }

    @Override
    public void publish(PaymentFailedEvent domainEvent) {
        if (domainEvent == null) {
            throw new PaymentDomainException("Cannot process null payment exception");
        }

        String simpleDomainName = domainEvent.getClass().getSimpleName();
        final String orderId = domainEvent.getPayment().getOrderId().toString();
        log.info("Received {} for order id: {}", simpleDomainName, orderId);

        final PaymentResponseAvroModel paymentResponseAvroModel = paymentMessagingMapper.paymentResponseAvroModelFromPaymentFailedEvent(domainEvent);
        final String simpleAvroModelName = paymentResponseAvroModel.getClass().getSimpleName();

        try {
            kafkaProducer.send(
                    paymentServiceConfigData.getPaymentResponseTopicName(),
                    orderId,
                    paymentResponseAvroModel,
                    kafkaPublisherCommons.getKafkaCallback(
                            paymentServiceConfigData.getPaymentResponseTopicName(),
                            paymentResponseAvroModel,
                            orderId,
                            simpleAvroModelName
                    )
            );

            log.info("{} sent to kafka for order id: {}", simpleAvroModelName, orderId);
        } catch (Exception e) {
            log.error("Error while sending {} to kafka with order id: {}, error: {}",
                    simpleAvroModelName, orderId, e.getMessage());
        }
    }
}

