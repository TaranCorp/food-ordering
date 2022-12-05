package org.food.ordering.restaurant.service.domain.entity;

import org.food.ordering.domain.entity.AggregateRoot;
import org.food.ordering.domain.valueobject.Money;
import org.food.ordering.domain.valueobject.OrderApprovalStatus;
import org.food.ordering.domain.valueobject.OrderStatus;
import org.food.ordering.domain.valueobject.RestaurantId;
import org.food.ordering.restaurant.service.domain.valueobject.OrderApprovalId;

import java.util.List;
import java.util.UUID;

public class Restaurant extends AggregateRoot<RestaurantId> {
    private OrderApproval orderApproval;
    private boolean active;
    private final OrderDetail orderDetail;

    private Restaurant(Builder builder) {
        super.setId(builder.id);
        orderApproval = builder.orderApproval;
        setActive(builder.active);
        orderDetail = builder.orderDetail;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void validateOrder(List<String> failureMessages) {
        if (orderDetail.getOrderStatus() != OrderStatus.PAID) {
            failureMessages.add("Payment is not completed for order: %s".formatted(orderDetail.getId()));
        }

        Money totalAmount = orderDetail.getProducts().stream()
                .map(product -> {
                    if (!product.isAvailable()) {
                        failureMessages.add("Product with id %s is not available".formatted(product.getId()));
                    }
                    return product.getPrice().multiply(product.getQuantity());
                })
                .reduce(Money.ZERO, Money::add);

        if (!totalAmount.equals(orderDetail.getTotalAmount())) {
            failureMessages.add("Price total is not correct for order with id %s".formatted(orderDetail.getId()));
        }
    }

    public void constructOrderApproval(OrderApprovalStatus orderApprovalStatus) {
        this.orderApproval = new OrderApproval(
                new OrderApprovalId(UUID.randomUUID()),
                getId(),
                orderDetail.getId(),
                orderApprovalStatus
        );
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public OrderApproval getOrderApproval() {
        return orderApproval;
    }

    public boolean isActive() {
        return active;
    }

    public OrderDetail getOrderDetail() {
        return orderDetail;
    }

    public static final class Builder {
        private RestaurantId id;
        private OrderApproval orderApproval;
        private boolean active;
        private OrderDetail orderDetail;

        private Builder() {
        }

        public Builder id(RestaurantId val) {
            id = val;
            return this;
        }

        public Builder orderApproval(OrderApproval val) {
            orderApproval = val;
            return this;
        }

        public Builder active(boolean val) {
            active = val;
            return this;
        }

        public Builder orderDetail(OrderDetail val) {
            orderDetail = val;
            return this;
        }

        public Restaurant build() {
            return new Restaurant(this);
        }
    }
}
