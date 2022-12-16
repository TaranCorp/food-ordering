package com.food.ordering.system.order.service.domain.mapper;

import com.food.ordering.system.domain.util.UuidUtils;
import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventProduct;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.food.ordering.system.domain.util.UuidUtils.extractId;

@Component
public class OrderDataMapper {

    public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
        return Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(createOrderCommand.getItems().stream().map(orderItem ->
                        new Product(new ProductId(orderItem.getProductId())))
                        .collect(Collectors.toList()))
                .build();
    }
    
    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                .customerId(new CustomerId(createOrderCommand.getCustomerId()))
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .deliveryAddress(orderAddressToStreetAddress(createOrderCommand.getAddress()))
                .price(new Money(createOrderCommand.getPrice()))
                .items(orderItemsToOrderItemEntities(createOrderCommand.getItems()))
                .build();
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order order, String message) {
        return CreateOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .message(message)
                .build();
    }

    public TrackOrderResponse orderToTrackOrderResponse(Order order) {
        return TrackOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages())
                .build();
    }

    private List<OrderItem> orderItemsToOrderItemEntities(
            List<com.food.ordering.system.order.service.domain.dto.create.OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem ->
                        OrderItem.builder()
                                .product(new Product(new ProductId(orderItem.getProductId())))
                                .price(new Money(orderItem.getPrice()))
                                .quantity(orderItem.getQuantity())
                                .subTotal(new Money(orderItem.getSubTotal()))
                                .build()).collect(Collectors.toList());
    }

    private StreetAddress orderAddressToStreetAddress(OrderAddress orderAddress) {
        return new StreetAddress(
                UUID.randomUUID(),
                orderAddress.getStreet(),
                orderAddress.getPostalCode(),
                orderAddress.getCity()
        );
    }

    public OrderPaymentEventPayload orderPaymentEventPayloadFromCreatedOrderResponse(OrderCreatedEvent orderCreatedEvent) {
        return OrderPaymentEventPayload.builder()
                .orderId(extractId(orderCreatedEvent.getOrder().getId()))
                .customerId(extractId(orderCreatedEvent.getOrder().getCustomerId()))
                .price(orderCreatedEvent.getOrder().getPrice().getAmount())
                .createdAt(orderCreatedEvent.getCreatedAt())
                .paymentOrderStatus(PaymentOrderStatus.PENDING.name())
                .build();
    }

    public OrderApprovalEventPayload orderApprovalEventPayloadFromOrderPaidEvent(OrderPaidEvent domainEvent) {
        return OrderApprovalEventPayload.builder()
                .orderId(extractId(domainEvent.getOrder().getId()))
                .restaurantId(extractId(domainEvent.getOrder().getRestaurantId()))
                .restaurantOrderStatus(RestaurantOrderStatus.PAID.name())
                .price(domainEvent.getOrder().getPrice().getAmount())
                .createdAt(domainEvent.getCreatedAt())
                .products(domainEvent.getOrder().getItems().stream()
                        .map(item -> new OrderApprovalEventProduct(
                                    item.getId().getValue().toString(),
                                    item.getQuantity()
                            )
                        )
                        .toList())
                .build();
    }

    public OrderPaymentEventPayload orderPaymentEventPayloadFromOrderCancelledEvent(OrderCancelledEvent orderCancelledEvent) {
        final Order order = orderCancelledEvent.getOrder();
        Assert.notNull(order, "OrderCancelledEvent is null");
        return OrderPaymentEventPayload.builder()
                .orderId(extractId(order.getId()))
                .customerId(extractId(order.getCustomerId()))
                .price(order.getPrice().getAmount())
                .createdAt(orderCancelledEvent.getCreatedAt())
                .paymentOrderStatus(PaymentOrderStatus.CANCELLED.name())
                .build();
    }
}
