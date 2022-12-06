package org.food.ordering.dataaccess.restaurant.repository;

import org.food.ordering.dataaccess.restaurant.entity.RestaurantEntity;
import org.food.ordering.dataaccess.restaurant.entity.RestaurantEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantJpaRepository extends JpaRepository<RestaurantEntity, RestaurantEntityId> {
    List<RestaurantEntity> findByRestaurantIdAndProductIdIn(UUID restaurantId, List<UUID> productIds);
}
