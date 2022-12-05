package org.food.ordering.dataaccess.restaurant.entity;


import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class RestaurantEntityId implements Serializable {
    private UUID restaurantId;
    private UUID productId;

    public RestaurantEntityId() {
    }

    public RestaurantEntityId(UUID restaurantId, UUID productId) {
        this.restaurantId = restaurantId;
        this.productId = productId;
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(UUID restaurantId) {
        this.restaurantId = restaurantId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantEntityId that = (RestaurantEntityId) o;
        return Objects.equals(restaurantId, that.restaurantId) && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restaurantId, productId);
    }
}
