package org.food.ordering.domain;

import lombok.extern.slf4j.Slf4j;
import org.food.ordering.domain.entity.Order;
import org.food.ordering.domain.entity.Product;
import org.food.ordering.domain.entity.Restaurant;
import org.food.ordering.domain.event.OrderCancelledEvent;
import org.food.ordering.domain.event.OrderCreatedEvent;
import org.food.ordering.domain.event.OrderPaidEvent;
import org.food.ordering.domain.exception.OrderDomainException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService {

    @Override
    public OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant) {
        validateRestaurant(restaurant);
        setOrderProductInformation(order, restaurant);
        order.validateOrder();
        order.initializeOrder();
        log.info("Order with id: {} is initiated", order.getId().getValue());
        return new OrderCreatedEvent(order);
    }

    private void validateRestaurant(Restaurant restaurant) {
        if (!restaurant.isActive()) {
            throw new OrderDomainException("Restaurant with id " + restaurant.getId().getValue() + " is currently not active");
        }
    }

    private void setOrderProductInformation(Order order, Restaurant restaurant) {
        final Map<Product, Product> restaurantProducts = restaurant.getProducts().stream()
                .collect(Collectors.toMap(Function.identity(), Function.identity()));

        order.getItems().forEach(item -> {
            Product orderProduct = item.getProduct();
            if (restaurantProducts.containsKey(orderProduct)) {
                Product restaurantProduct = restaurantProducts.get(orderProduct);
                orderProduct.updateProductInfo(
                        restaurantProduct.getName(),
                        restaurantProduct.getPrice()
                );
            }
        });
    }

    @Override
    public OrderPaidEvent payOrder(Order order) {
        order.pay();
        log.info("Order with id {} is paid", order.getId().getValue());
        return new OrderPaidEvent(order);
    }

    @Override
    public void approveOrder(Order order) {
        order.approve();
        log.info("Order with id {} is approved", order.getId().getValue());
    }

    @Override
    public OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages) {
        order.initCancel(failureMessages);
        log.info("Order with id {} is cancelling", order.getId().getValue());
        return new OrderCancelledEvent(order);
    }

    @Override
    public void cancelOrder(Order order, List<String> failureMessages) {
        order.cancel(failureMessages);
        log.info("Order with id {} is cancelled", order.getId().getValue());
    }
}
