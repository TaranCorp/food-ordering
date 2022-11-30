package org.food.ordering.order.service.dataaccess.restaurant.repository;

import org.food.ordering.order.service.dataaccess.restaurant.entity.RestaurantEntity;
import org.food.ordering.order.service.dataaccess.restaurant.entity.RestaurantEntityId;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface RestaurantJpaRepository extends Repository<RestaurantEntity, RestaurantEntityId> {
    List<RestaurantEntity> findByRestaurantIdAndProductIdIn(UUID restaurantId, List<UUID> productIds);
}
