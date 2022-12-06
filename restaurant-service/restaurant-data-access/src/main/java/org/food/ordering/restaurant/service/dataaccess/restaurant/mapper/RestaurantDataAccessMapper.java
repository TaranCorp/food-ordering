package org.food.ordering.restaurant.service.dataaccess.restaurant.mapper;

import org.food.ordering.dataaccess.restaurant.entity.RestaurantEntity;
import org.food.ordering.domain.valueobject.Money;
import org.food.ordering.domain.valueobject.OrderId;
import org.food.ordering.domain.valueobject.ProductId;
import org.food.ordering.domain.valueobject.RestaurantId;
import org.food.ordering.restaurant.service.dataaccess.restaurant.entity.OrderApprovalEntity;
import org.food.ordering.restaurant.service.domain.entity.OrderApproval;
import org.food.ordering.restaurant.service.domain.entity.OrderDetail;
import org.food.ordering.restaurant.service.domain.entity.Product;
import org.food.ordering.restaurant.service.domain.entity.Restaurant;
import org.food.ordering.restaurant.service.domain.exception.RestaurantNotFoundException;
import org.food.ordering.restaurant.service.domain.valueobject.OrderApprovalId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RestaurantDataAccessMapper {
    public Restaurant restaurantFromRestaurantEntity(List<RestaurantEntity> restaurantEntities) {
        final RestaurantEntity restaurantEntity = restaurantEntities.stream()
                .findFirst()
                .orElseThrow(() -> new RestaurantNotFoundException("No restaurants found"));

        List<Product> products = restaurantEntities.stream()
                .map(product -> new Product(
                            new ProductId(product.getProductId()),
                            product.getProductName(),
                            new Money(product.getProductPrice()),
                            product.getProductActive()
                        )
                )
                .toList();

        return Restaurant.builder()
                .id(new RestaurantId(restaurantEntity.getRestaurantId()))
                .active(restaurantEntity.isRestaurantActive())
                .orderDetail(OrderDetail.builder()
                        .products(products)
                        .build())
                .build();
    }

    public OrderApprovalEntity orderApprovalEntityFromOrderApproval(OrderApproval orderApproval) {
        return new OrderApprovalEntity(
                orderApproval.getOrderId().getValue(),
                orderApproval.getRestaurantId().getValue(),
                orderApproval.getOrderId().getValue(),
                orderApproval.getApprovalStatus()
        );
    }

    public OrderApproval orderApprovalFromOrderApprovalEntity(OrderApprovalEntity entity) {
        return new OrderApproval(
                new OrderApprovalId(entity.getId()),
                new RestaurantId(entity.getRestaurantId()),
                new OrderId(entity.getOrderId()),
                entity.getOrderApprovalStatus()
        );
    }
}
