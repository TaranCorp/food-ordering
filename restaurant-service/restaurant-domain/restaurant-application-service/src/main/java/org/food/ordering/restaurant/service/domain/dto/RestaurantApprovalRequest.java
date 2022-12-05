package org.food.ordering.restaurant.service.domain.dto;

import org.food.ordering.restaurant.service.domain.entity.Product;
import org.food.ordering.domain.valueobject.RestaurantOrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class RestaurantApprovalRequest {
    private String id;
    private String sagaId;
    private String restaurantId;
    private String orderId;
    private RestaurantOrderStatus restaurantOrderStatus;
    private List<Product> products;
    private BigDecimal price;
    private Instant createdAt;

    public RestaurantApprovalRequest(String id,
                                     String sagaId,
                                     String restaurantId,
                                     String orderId,
                                     RestaurantOrderStatus restaurantOrderStatus,
                                     List<Product> products,
                                     BigDecimal price,
                                     Instant createdAt) {
        this.id = id;
        this.sagaId = sagaId;
        this.restaurantId = restaurantId;
        this.orderId = orderId;
        this.restaurantOrderStatus = restaurantOrderStatus;
        this.products = products;
        this.price = price;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSagaId() {
        return sagaId;
    }

    public void setSagaId(String sagaId) {
        this.sagaId = sagaId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public RestaurantOrderStatus getRestaurantOrderStatus() {
        return restaurantOrderStatus;
    }

    public void setRestaurantOrderStatus(RestaurantOrderStatus restaurantOrderStatus) {
        this.restaurantOrderStatus = restaurantOrderStatus;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
