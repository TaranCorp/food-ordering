package org.food.ordering.restaurant.service.dataaccess.restaurant.adapter;

import org.food.ordering.dataaccess.restaurant.entity.RestaurantEntity;
import org.food.ordering.dataaccess.restaurant.repository.RestaurantJpaRepository;
import org.food.ordering.restaurant.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import org.food.ordering.restaurant.service.domain.entity.Product;
import org.food.ordering.restaurant.service.domain.entity.Restaurant;
import org.food.ordering.restaurant.service.domain.port.output.repository.RestaurantRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Repository
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository repository;
    private final RestaurantDataAccessMapper mapper;

    public RestaurantRepositoryImpl(RestaurantJpaRepository repository, RestaurantDataAccessMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Restaurant> findRestaurant(Restaurant restaurant) {
        Optional<List<RestaurantEntity>> restaurantEntities = Optional.of(repository.findByRestaurantIdAndProductIdIn(
                restaurant.getId().getValue(),
                getProductsId(restaurant)
        ));
        return restaurantEntities.map(mapper::restaurantFromRestaurantEntity);
    }

    private List<UUID> getProductsId(Restaurant restaurant) {
        return restaurant.getOrderDetail().getProducts().stream()
                .map(idFromProduct())
                .toList();
    }

    private Function<Product, UUID> idFromProduct() {
        return product -> product.getId().getValue();
    }
}
