package org.food.ordering.restaurant.service.domain.entity;

import org.food.ordering.domain.entity.BaseEntity;
import org.food.ordering.domain.valueobject.Money;
import org.food.ordering.domain.valueobject.ProductId;

public class Product extends BaseEntity<ProductId> {
    private String name;
    private Money price;
    private int quantity;
    private boolean available;

    public Product(ProductId productId, String name, Money price, boolean available) {
        super.setId(productId);
        this.name = name;
        this.price = price;
        this.available = available;
    }

    public Product(ProductId productId, int quantity) {
        super.setId(productId);
        this.quantity = quantity;
    }

    public void updateWithConfirmedNamePriceAndAvailability(String name, Money price, boolean available) {
        this.name = name;
        this.price = price;
        this.available = available;
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isAvailable() {
        return available;
    }
}
