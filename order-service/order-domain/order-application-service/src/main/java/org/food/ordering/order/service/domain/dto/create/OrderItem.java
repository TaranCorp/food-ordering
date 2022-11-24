package org.food.ordering.order.service.domain.dto.create;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public class OrderItem {

    @NotNull
    private final UUID productId;

    @NotNull
    private final Integer quantity;

    @NotNull
    private final BigDecimal price;

    @NotNull
    private final BigDecimal subTotal;

    public OrderItem(UUID productId, Integer quantity, BigDecimal price, BigDecimal subTotal) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.subTotal = subTotal;
    }

    private OrderItem(Builder builder) {
        productId = builder.productId;
        quantity = builder.quantity;
        price = builder.price;
        subTotal = builder.subTotal;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public static final class Builder {
        private @NotNull UUID productId;
        private @NotNull Integer quantity;
        private @NotNull BigDecimal price;
        private @NotNull BigDecimal subTotal;

        private Builder() {
        }

        public Builder productId(@NotNull UUID val) {
            productId = val;
            return this;
        }

        public Builder quantity(@NotNull Integer val) {
            quantity = val;
            return this;
        }

        public Builder price(@NotNull BigDecimal val) {
            price = val;
            return this;
        }

        public Builder subTotal(@NotNull BigDecimal val) {
            subTotal = val;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}
