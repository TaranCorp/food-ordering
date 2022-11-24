package org.food.ordering.domain.entity;

import org.food.ordering.domain.exception.OrderDomainException;
import org.food.ordering.domain.valueobject.CustomerId;
import org.food.ordering.domain.valueobject.Money;
import org.food.ordering.domain.valueobject.OrderId;
import org.food.ordering.domain.valueobject.OrderItemId;
import org.food.ordering.domain.valueobject.OrderStatus;
import org.food.ordering.domain.valueobject.RestaurantId;
import org.food.ordering.domain.valueobject.StreetAddress;
import org.food.ordering.domain.valueobject.TrackingId;

import java.util.List;
import java.util.UUID;

import static org.food.ordering.domain.valueobject.OrderStatus.APPROVED;
import static org.food.ordering.domain.valueobject.OrderStatus.CANCELLED;
import static org.food.ordering.domain.valueobject.OrderStatus.CANCELLING;
import static org.food.ordering.domain.valueobject.OrderStatus.PAID;
import static org.food.ordering.domain.valueobject.OrderStatus.PENDING;

public class Order extends AggregateRoot<OrderId> {
    private final CustomerId customerId;
    private final RestaurantId restaurantId;
    private final StreetAddress deliveryAddress;
    private final Money price;
    private final List<OrderItem> items;

    private TrackingId trackingId;
    private OrderStatus orderStatus;
    private List<String> failureMessages;

    private Order(Builder builder) {
        super.setId(builder.orderId);
        customerId = builder.customerId;
        restaurantId = builder.restaurantId;
        deliveryAddress = builder.deliveryAddress;
        price = builder.price;
        items = builder.items;
        trackingId = builder.trackingId;
        orderStatus = builder.orderStatus;
        failureMessages = builder.failureMessages;
    }

    public void initializeOrder() {
        setId(new OrderId(UUID.randomUUID()));
        trackingId = new TrackingId(UUID.randomUUID());
        orderStatus = PENDING;
        initializeOrderItems();
    }

    private void initializeOrderItems() {
        long itemId = 1;
        for (OrderItem orderItem: items) {
            orderItem.initializeOrderItem(super.getId(), new OrderItemId(itemId++));
        }
    }

    public void pay() {
        if (orderStatus != PENDING) {
            throw new OrderDomainException("Order is not in correct state for pay operation");
        }
        orderStatus = PAID;
    }

    public void approve() {
        if (orderStatus != PAID) {
            throw new OrderDomainException("Order is not in correct state for approve operation");
        }
        orderStatus = APPROVED;
    }

    public void initCancel(List<String> failureMessages) {
        if (orderStatus != PAID) {
            throw new OrderDomainException("Order is not in correct state for cancelling operation");
        }
        orderStatus = CANCELLING;
        updateFailureMessages(failureMessages);
    }

    public void cancel(List<String> failureMessages) {
        if (!(orderStatus == CANCELLING || orderStatus == PENDING)) {
            throw new OrderDomainException("Order is not in correct state for cancel operation");
        }
        orderStatus = CANCELLED;
        updateFailureMessages(failureMessages);
    }

    private void updateFailureMessages(List<String> failureMessages) {
        if (this.failureMessages != null && failureMessages != null) {
            this.failureMessages.addAll(filterNotEmptyMessages(failureMessages));
        }
        if (this.failureMessages == null && failureMessages != null) {
            this.failureMessages = failureMessages;
        }
    }

    private List<String> filterNotEmptyMessages(List<String> failureMessages) {
        return failureMessages.stream().filter(message -> !message.isEmpty()).toList();
    }

    public void validateOrder() {
        validateInitialOrder();
        validateTotalPrice();
        validateItemsPrice();
    }

    private void validateInitialOrder() {
        if (orderStatus != null || getId() != null) {
            throw new OrderDomainException("Order is not in correct state for initialization!");
        }
    }

    private void validateTotalPrice() {
        if (price == null || !price.isGreaterThanZero()) {
            throw new OrderDomainException("Total price must be greater than zero!");
        }
    }

    private void validateItemsPrice() {
        final Money orderItemsTotal = items.stream().map(item -> {
                                            item.validatePrice();
                                            return item.getSubTotal();
                                        }).reduce(Money.ZERO, Money::add);

        if (!price.equals(orderItemsTotal)) {
            throw new OrderDomainException(String.format(
                    "Total price: %s is not equal to orderItems total: %s",
                    price.getAmount(), orderItemsTotal.getAmount()
            ));
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public StreetAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public Money getPrice() {
        return price;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }

    public static final class Builder {
        private OrderId orderId;
        private CustomerId customerId;
        private RestaurantId restaurantId;
        private StreetAddress deliveryAddress;
        private Money price;
        private List<OrderItem> items;
        private TrackingId trackingId;
        private OrderStatus orderStatus;
        private List<String> failureMessages;

        private Builder() {
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder restaurantId(RestaurantId val) {
            restaurantId = val;
            return this;
        }

        public Builder deliveryAddress(StreetAddress val) {
            deliveryAddress = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder items(List<OrderItem> val) {
            items = val;
            return this;
        }

        public Builder trackingId(TrackingId val) {
            trackingId = val;
            return this;
        }

        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder failureMessages(List<String> val) {
            failureMessages = val;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}