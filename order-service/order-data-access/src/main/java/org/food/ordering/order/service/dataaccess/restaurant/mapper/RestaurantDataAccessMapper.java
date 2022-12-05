package org.food.ordering.order.service.dataaccess.restaurant.mapper;

import org.food.ordering.dataaccess.restaurant.entity.RestaurantEntity;
import org.food.ordering.dataaccess.restaurant.exception.NotFoundException;
import org.food.ordering.domain.entity.Product;
import org.food.ordering.domain.entity.Restaurant;
import org.food.ordering.domain.valueobject.Money;
import org.food.ordering.domain.valueobject.ProductId;
import org.food.ordering.domain.valueobject.RestaurantId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Component
public class RestaurantDataAccessMapper {

    public List<UUID> restaurantProductIdsFromRestaurant(Restaurant restaurant) {
        return restaurant.getProducts().stream()
                .map(product -> product.getId().getValue())
                .toList();
    }

    public Restaurant restaurantFromRestaurantEntity(List<RestaurantEntity> restaurantEntities) {
        RestaurantEntity restaurantEntity = restaurantEntities.stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Could not find restaurant"));

        List<Product> restaurantProducts = restaurantEntities.stream()
                .map(productFromRestaurant())
                .toList();

        return Restaurant.builder()
                .restaurantId(new RestaurantId(restaurantEntity.getRestaurantId()))
                .active(restaurantEntity.isRestaurantActive())
                .products(restaurantProducts)
                .build();
    }

    private Function<RestaurantEntity, Product> productFromRestaurant() {
        return entity -> new Product(
                new ProductId(entity.getProductId()),
                entity.getProductName(),
                new Money(entity.getProductPrice()));
    }
}
