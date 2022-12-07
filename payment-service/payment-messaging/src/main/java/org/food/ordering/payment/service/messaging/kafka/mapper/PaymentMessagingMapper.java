package org.food.ordering.payment.service.messaging.kafka.mapper;

import org.food.ordering.domain.valueobject.PaymentOrderStatus;
import org.food.ordering.kafka.order.avro.model.PaymentRequestAvroModel;
import org.food.ordering.kafka.order.avro.model.PaymentResponseAvroModel;
import org.food.ordering.kafka.order.avro.model.PaymentStatus;
import org.food.ordering.payment.service.domain.dto.PaymentRequest;
import org.food.ordering.payment.service.domain.entity.Payment;
import org.food.ordering.payment.service.domain.event.PaymentCancelledEvent;
import org.food.ordering.payment.service.domain.event.PaymentCompletedEvent;
import org.food.ordering.payment.service.domain.event.PaymentFailedEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentMessagingMapper {
    public PaymentResponseAvroModel paymentResponseAvroModelFromPaymentCompletedEvent(PaymentCompletedEvent domainEvent) {
        Payment payment = domainEvent.getPayment();
        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setCreatedAt(domainEvent.getCreatedAt().toInstant())
                .setCustomerId(payment.getCustomerId().getValue().toString())
                .setOrderId(payment.getOrderId().getValue().toString())
                .setPrice(payment.getPrice().getAmount())
                .setPaymentStatus(PaymentStatus.valueOf(payment.getPaymentStatus().name()))
                .setPaymentId(payment.getId().toString())
                .setFailureMessages(domainEvent.getFailureMessages())
                .build();
    }

    public PaymentResponseAvroModel paymentResponseAvroModelFromPaymentCancelledEvent(PaymentCancelledEvent domainEvent) {
        Payment payment = domainEvent.getPayment();
        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setPaymentId(payment.getId().toString())
                .setFailureMessages(domainEvent.getFailureMessages())
                .setCreatedAt(domainEvent.getCreatedAt().toInstant())
                .setPrice(payment.getPrice().getAmount())
                .setPaymentStatus(PaymentStatus.valueOf(payment.getPaymentStatus().name()))
                .setOrderId(payment.getOrderId().getValue().toString())
                .setCustomerId(payment.getOrderId().getValue().toString())
                .build();
    }

    public PaymentResponseAvroModel paymentResponseAvroModelFromPaymentFailedEvent(PaymentFailedEvent domainEvent) {
        Payment payment = domainEvent.getPayment();
        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setPaymentId(payment.getId().toString())
                .setFailureMessages(domainEvent.getFailureMessages())
                .setCreatedAt(domainEvent.getCreatedAt().toInstant())
                .setPrice(payment.getPrice().getAmount())
                .setPaymentStatus(PaymentStatus.valueOf(payment.getPaymentStatus().name()))
                .setOrderId(payment.getOrderId().getValue().toString())
                .setCustomerId(payment.getOrderId().getValue().toString())
                .build();
    }

    public PaymentRequest paymentRequestFromPaymentRequestAvroModel(PaymentRequestAvroModel paymentRequestAvroModel) {
        return PaymentRequest.builder()
                .id(paymentRequestAvroModel.getId())
                .createdAt(paymentRequestAvroModel.getCreatedAt())
                .orderId(paymentRequestAvroModel.getOrderId())
                .sagaId(paymentRequestAvroModel.getSagaId())
                .price(paymentRequestAvroModel.getPrice())
                .paymentOrderStatus(PaymentOrderStatus.valueOf(paymentRequestAvroModel.getPaymentOrderStatus().name()))
                .customerId(paymentRequestAvroModel.getCustomerId())
                .build();
    }
}
