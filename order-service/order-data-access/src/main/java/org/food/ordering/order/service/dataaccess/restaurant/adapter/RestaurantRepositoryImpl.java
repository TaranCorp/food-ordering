package org.food.ordering.order.service.dataaccess.restaurant.adapter;

import org.food.ordering.dataaccess.restaurant.entity.RestaurantEntity;
import org.food.ordering.dataaccess.restaurant.repository.RestaurantJpaRepository;
import org.food.ordering.domain.entity.Restaurant;
import org.food.ordering.order.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import org.food.ordering.order.service.domain.port.output.repository.RestaurantRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository repository;
    private final RestaurantDataAccessMapper mapper;

    public RestaurantRepositoryImpl(RestaurantJpaRepository repository, RestaurantDataAccessMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {
        List<UUID> productIds = mapper.restaurantProductIdsFromRestaurant(restaurant);
        List<RestaurantEntity> restaurants = repository.findByRestaurantIdAndProductIdIn(restaurant.getId().getValue(), productIds);
        return Optional.of(mapper.restaurantFromRestaurantEntity(restaurants));
    }
}
