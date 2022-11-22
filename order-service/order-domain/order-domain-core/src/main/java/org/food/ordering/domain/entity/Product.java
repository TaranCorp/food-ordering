package org.food.ordering.domain.entity;

import org.food.ordering.domain.valueobject.Money;
import org.food.ordering.domain.valueobject.ProductId;

public class Product extends BaseEntity<ProductId> {
    private String name;
    private Money price;

    public Product(ProductId productId, String name, Money price) {
        super.setId(productId);
        this.name = name;
        this.price = price;
    }

    public Product(ProductId productId) {
        super.setId(productId);
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    public void updateProductInfo(String name, Money price) {
        this.name = name;
        this.price = price;
    }
}
