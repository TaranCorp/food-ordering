package org.food.ordering.order.service.domain.dto.create;

import org.food.ordering.domain.valueobject.OrderStatus;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class CreateOrderResponse {

    @NotNull
    private final UUID orderTrackingId;

    @NotNull
    private final OrderStatus orderStatus;

    @NotNull
    private final String message;

    public CreateOrderResponse(UUID orderTrackingId, OrderStatus orderStatus, String message) {
        this.orderTrackingId = orderTrackingId;
        this.orderStatus = orderStatus;
        this.message = message;
    }

    public static Builder builder() {
        return new Builder();
    }

    private CreateOrderResponse(Builder builder) {
        orderTrackingId = builder.orderTrackingId;
        orderStatus = builder.orderStatus;
        message = builder.message;
    }

    public UUID getOrderTrackingId() {
        return orderTrackingId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public String getMessage() {
        return message;
    }

    public static final class Builder {
        private @NotNull UUID orderTrackingId;
        private @NotNull OrderStatus orderStatus;
        private @NotNull String message;

        private Builder() {
        }

        public Builder orderTrackingId(@NotNull UUID val) {
            orderTrackingId = val;
            return this;
        }

        public Builder orderStatus(@NotNull OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder message(@NotNull String val) {
            message = val;
            return this;
        }

        public CreateOrderResponse build() {
            return new CreateOrderResponse(this);
        }
    }
}
