package org.food.ordering.order.service.dataaccess.order.mapper;

import org.food.ordering.domain.entity.Order;
import org.food.ordering.domain.entity.OrderItem;
import org.food.ordering.domain.valueobject.CustomerId;
import org.food.ordering.domain.valueobject.Money;
import org.food.ordering.domain.valueobject.OrderId;
import org.food.ordering.domain.valueobject.OrderItemId;
import org.food.ordering.domain.valueobject.RestaurantId;
import org.food.ordering.domain.valueobject.StreetAddress;
import org.food.ordering.domain.valueobject.TrackingId;
import org.food.ordering.order.service.dataaccess.order.entity.OrderAddressEntity;
import org.food.ordering.order.service.dataaccess.order.entity.OrderEntity;
import org.food.ordering.order.service.dataaccess.order.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.food.ordering.domain.entity.Product.createProductUsingId;
import static org.food.ordering.order.service.dataaccess.order.entity.OrderEntity.FAILURE_MESSAGES_DELIMITER;

@Component
public class OrderDataAccessMapper {

    private static final String DEFAULT_STRING_RESULT = "";

    public OrderEntity orderEntityFromOrder(Order order) {
        OrderEntity orderEntity = new OrderEntity(
                order.getId().getValue(),
                order.getCustomerId().getValue(),
                order.getRestaurantId().getValue(),
                order.getTrackingId().getValue(),
                order.getPrice().getAmount(),
                reduceFailureMessages(order.getFailureMessages()),
                order.getOrderStatus(),
                addressEntityFromDeliveryAddress(order.getDeliveryAddress()),
                orderItemEntitiesFromOrderItems(order.getItems())
        );
        orderEntity.getOrderAddress().setOrder(orderEntity);

        for (OrderItemEntity item : orderEntity.getItems()) {
            item.setOrder(orderEntity);
        }

        return orderEntity;
    }

    private String reduceFailureMessages(List<String> failureMessages) {
        return failureMessages != null
                ? String.join(FAILURE_MESSAGES_DELIMITER, failureMessages)
                : DEFAULT_STRING_RESULT;
    }

    private OrderAddressEntity addressEntityFromDeliveryAddress(StreetAddress deliveryAddress) {
        return new OrderAddressEntity(
                deliveryAddress.getId(),
                deliveryAddress.getStreet(),
                deliveryAddress.getPostalCode(),
                deliveryAddress.getCity()
        );
    }

    private List<OrderItemEntity> orderItemEntitiesFromOrderItems(List<OrderItem> items) {
        return items.stream()
                .map(this::orderItemEntityFromOrderItem)
                .toList();
    }

    private OrderItemEntity orderItemEntityFromOrderItem(OrderItem item) {
        return new OrderItemEntity(
                item.getId().getValue(),
                item.getProduct().getId().getValue(),
                item.getPrice().getAmount(),
                item.getQuantity(),
                item.getSubTotal().getAmount()
        );
    }

    public Order orderFromOrderEntity(OrderEntity orderEntity) {
        return Order.builder()
                .orderId(new OrderId(orderEntity.getId()))
                .restaurantId(new RestaurantId(orderEntity.getRestaurantId()))
                .customerId(new CustomerId(orderEntity.getCustomerId()))
                .trackingId(new TrackingId(orderEntity.getTrackingId()))
                .price(new Money(orderEntity.getPrice()))
                .orderStatus(orderEntity.getOrderStatus())
                .deliveryAddress(deliveryAddressFromAddressEntity(orderEntity.getOrderAddress()))
                .items(orderItemsFromOrderItemEntities(orderEntity.getItems()))
                .failureMessages(splitFailureMessages(orderEntity.getFailureMessages()))
                .build();
    }

    private StreetAddress deliveryAddressFromAddressEntity(OrderAddressEntity orderAddress) {
        return new StreetAddress(
                orderAddress.getId(),
                orderAddress.getStreet(),
                orderAddress.getPostalCode(),
                orderAddress.getCity()
        );
    }

    private List<OrderItem> orderItemsFromOrderItemEntities(List<OrderItemEntity> items) {
        return items.stream()
                .map(this::orderItemFromOrderItemEntity)
                .toList();
    }

    private OrderItem orderItemFromOrderItemEntity(OrderItemEntity orderItemEntity) {
        return OrderItem.builder()
                .id(new OrderItemId(orderItemEntity.getId()))
                .price(new Money(orderItemEntity.getPrice()))
                .subTotal(new Money(orderItemEntity.getSubTotal()))
                .quantity(orderItemEntity.getQuantity())
                .subTotal(new Money(orderItemEntity.getSubTotal()))
                .product(createProductUsingId(orderItemEntity.getProductId()))
                .build();
    }

    private List<String> splitFailureMessages(String failureMessages) {
        return failureMessages.isEmpty()
                               ? new ArrayList<>()
                               : new ArrayList<>(Arrays.asList(failureMessages.split(FAILURE_MESSAGES_DELIMITER)));
    }
}
