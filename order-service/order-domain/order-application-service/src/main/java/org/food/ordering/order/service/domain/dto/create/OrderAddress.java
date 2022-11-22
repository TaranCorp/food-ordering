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

    public String getStreet() {
        return street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }
}
