package org.food.ordering.restaurant.service.domain.port.output.repository;

import org.food.ordering.restaurant.service.domain.entity.Restaurant;

import java.util.Optional;

public interface RestaurantRepository {
    Optional<Restaurant> findRestaurant(Restaurant restaurant);
}
