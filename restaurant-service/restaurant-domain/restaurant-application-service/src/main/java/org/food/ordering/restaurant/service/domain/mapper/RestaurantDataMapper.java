package org.food.ordering.restaurant.service.domain.mapper;

import org.food.ordering.domain.valueobject.Money;
import org.food.ordering.domain.valueobject.OrderId;
import org.food.ordering.domain.valueobject.OrderStatus;
import org.food.ordering.domain.valueobject.ProductId;
import org.food.ordering.domain.valueobject.RestaurantId;
import org.food.ordering.restaurant.service.domain.dto.RestaurantApprovalRequest;
import org.food.ordering.restaurant.service.domain.entity.OrderDetail;
import org.food.ordering.restaurant.service.domain.entity.Product;
import org.food.ordering.restaurant.service.domain.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RestaurantDataMapper {
    public Restaurant createRestaurantFromRestaurantApprovalRequest(RestaurantApprovalRequest restaurantApprovalRequest) {
        return Restaurant.builder()
                .id(new RestaurantId(UUID.fromString(restaurantApprovalRequest.getRestaurantId())))
                .orderDetail(
                        OrderDetail.builder()
                                .id(new OrderId(UUID.fromString(restaurantApprovalRequest.getOrderId())))
                                .products(mapProductsToEntity(restaurantApprovalRequest))
                                .totalAmount(new Money(restaurantApprovalRequest.getPrice()))
                                .orderStatus(OrderStatus.valueOf(restaurantApprovalRequest.getRestaurantOrderStatus().name()))
                                .build()
                )
                .build();
    }

    private List<Product> mapProductsToEntity(RestaurantApprovalRequest restaurantApprovalRequest) {
        return restaurantApprovalRequest.getProducts().stream()
                .map(product -> new Product(
                                        product.getId(),
                                        product.getQuantity()
                        )
                ).toList();
    }
}
