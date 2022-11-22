package org.food.ordering.order.service.domain.mapper;

import org.food.ordering.domain.entity.Order;
import org.food.ordering.domain.entity.OrderItem;
import org.food.ordering.domain.entity.Product;
import org.food.ordering.domain.entity.Restaurant;
import org.food.ordering.domain.valueobject.CustomerId;
import org.food.ordering.domain.valueobject.Money;
import org.food.ordering.domain.valueobject.ProductId;
import org.food.ordering.domain.valueobject.RestaurantId;
import org.food.ordering.domain.valueobject.StreetAddress;
import org.food.ordering.order.service.domain.dto.create.CreateOrderCommand;
import org.food.ordering.order.service.domain.dto.create.CreateOrderResponse;
import org.food.ordering.order.service.domain.dto.create.OrderAddress;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OrderDataMapper {
    public Restaurant createRestaurantFromOrderCommand(CreateOrderCommand createOrderCommand) {
        return Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(createProductsFromOrder(createOrderCommand))
                .build();
    }

    private List<Product> createProductsFromOrder(CreateOrderCommand createOrderCommand) {
        return createOrderCommand.getItems().stream()
                .map(orderItem -> new Product(new ProductId(orderItem.getProductId())))
                .toList();
    }

    public CreateOrderResponse createOrderResponseFromOrder(Order order) {
        return CreateOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .build();
    }

    public Order createOrderFromOrderCommand(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                .customerId(new CustomerId(createOrderCommand.getCustomerId()))
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .deliveryAddress(streetAddressFromOrderAddress(createOrderCommand.getAddress()))
                .price(new Money(createOrderCommand.getPrice()))
                .items(orderItemEntitiesFromOrderItems(createOrderCommand))
                .build();
    }

    private List<OrderItem> orderItemEntitiesFromOrderItems(CreateOrderCommand createOrderCommand) {
        return createOrderCommand.getItems().stream()
                .map(this::orderItemEntityFromOrderItem)
                .toList();
    }

    private OrderItem orderItemEntityFromOrderItem(org.food.ordering.order.service.domain.dto.create.OrderItem item) {
        return OrderItem.builder()
                .product(new Product(new ProductId(item.getProductId())))
                .quantity(item.getQuantity())
                .price(new Money(item.getPrice()))
                .subTotal(new Money(item.getSubTotal()))
                .build();
    }

    private StreetAddress streetAddressFromOrderAddress(OrderAddress address) {
        return new StreetAddress(
                UUID.randomUUID(),
                address.getStreet(),
                address.getPostalCode(),
                address.getCity()
        );
    }
}
