package org.food.ordering.order.service.domain.dto.create;

import javax.validation.constraints.NotNull;

public class OrderAddress {

    @NotNull
    private final String street;

    @NotNull
    private final String postalCode;

    @NotNull
    private final String city;

    public OrderAddress(String street, String postalCode, String city) {
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
    }

    private OrderAddress(Builder builder) {
        street = builder.street;
        postalCode = builder.postalCode;
        city = builder.city;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getStreet() {
        return street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public static final class Builder {
        private @NotNull String street;
        private @NotNull String postalCode;
        private @NotNull String city;

        private Builder() {
        }

        public Builder street(@NotNull String val) {
            street = val;
            return this;
        }

        public Builder postalCode(@NotNull String val) {
            postalCode = val;
            return this;
        }

        public Builder city(@NotNull String val) {
            city = val;
            return this;
        }

        public OrderAddress build() {
            return new OrderAddress(this);
        }
    }
}
