package org.food.ordering.order.service.messaging.kafka.mapper;

import org.food.ordering.domain.entity.Order;
import org.food.ordering.domain.entity.OrderItem;
import org.food.ordering.domain.event.OrderCancelledEvent;
import org.food.ordering.domain.event.OrderCreatedEvent;
import org.food.ordering.domain.event.OrderPaidEvent;
import org.food.ordering.domain.valueobject.OrderApprovalStatus;
import org.food.ordering.domain.valueobject.PaymentStatus;
import org.food.ordering.kafka.order.avro.model.PaymentOrderStatus;
import org.food.ordering.kafka.order.avro.model.PaymentRequestAvroModel;
import org.food.ordering.kafka.order.avro.model.PaymentResponseAvroModel;
import org.food.ordering.kafka.order.avro.model.Product;
import org.food.ordering.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import org.food.ordering.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import org.food.ordering.kafka.order.avro.model.RestaurantOrderStatus;
import org.food.ordering.order.service.domain.dto.message.PaymentResponse;
import org.food.ordering.order.service.domain.dto.message.RestaurantApprovalResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.food.ordering.order.service.domain.util.DomainUtils.extractId;
import static org.food.ordering.order.service.domain.util.DomainUtils.extractInstant;

@Component
public class OrderMessagingDataMapper {

    public PaymentRequestAvroModel paymentRequestAvroModelFromOrderCreatedEvent(OrderCreatedEvent orderCreatedEvent) {
        Order order = orderCreatedEvent.getOrder();
        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setCustomerId(extractId(order.getCustomerId()))
                .setOrderId(extractId(order.getId()))
                .setCreatedAt(extractInstant(orderCreatedEvent))
                .setPrice(order.getPrice().getAmount())
                .setPaymentOrderStatus(PaymentOrderStatus.PENDING)
                .build();
    }

    public PaymentRequestAvroModel paymentRequestAvroModelFromOrderCancelledEvent(OrderCancelledEvent orderCancelledEvent) {
        Order order = orderCancelledEvent.getOrder();
        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setCustomerId(extractId(order.getCustomerId()))
                .setOrderId(extractId(order.getId()))
                .setCreatedAt(extractInstant(orderCancelledEvent))
                .setPrice(order.getPrice().getAmount())
                .setPaymentOrderStatus(PaymentOrderStatus.CANCELLED)
                .build();
    }

    public RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModelFromOrderPaidEvent(OrderPaidEvent domainEvent) {
        Order order = domainEvent.getOrder();
        return RestaurantApprovalRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setCreatedAt(domainEvent.getCreatedAt().toInstant())
                .setPrice(order.getPrice().getAmount())
                .setRestaurantOrderStatus(RestaurantOrderStatus.PAID)
                .setOrderId(extractId(order.getId()))
                .setRestaurantId(extractId(order.getRestaurantId()))
                .setProducts(mapItemsToProducts(order.getItems()))
                .build();
    }

    private List<Product> mapItemsToProducts(List<OrderItem> items) {
        return items.stream()
                .map(item -> new Product(extractId(item.getId()), item.getQuantity()))
                .toList();
    }

    public PaymentResponse paymentResponseFromPaymentResponseAvroModel(PaymentResponseAvroModel response) {
        return new PaymentResponse(
                response.getId(),
                response.getSagaId(),
                response.getOrderId(),
                response.getPaymentId(),
                response.getCustomerId(),
                response.getPrice(),
                response.getCreatedAt(),
                PaymentStatus.valueOf(response.getPaymentStatus().name()),
                response.getFailureMessages()
        );
    }

    public RestaurantApprovalResponse restaurantApprovalResponseFromRestaurantApprovalResponseAvroModel(RestaurantApprovalResponseAvroModel restaurantResponse) {
        return new RestaurantApprovalResponse(
                restaurantResponse.getId(),
                "",
                restaurantResponse.getOrderId(),
                restaurantResponse.getRestaurantId(),
                restaurantResponse.getCreatedAt(),
                OrderApprovalStatus.valueOf(restaurantResponse.getOrderApprovalStatus().name()),
                restaurantResponse.getFailureMessages()
        );
    }
}
