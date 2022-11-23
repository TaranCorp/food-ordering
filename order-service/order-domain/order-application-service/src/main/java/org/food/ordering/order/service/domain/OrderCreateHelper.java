package org.food.ordering.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.food.ordering.domain.OrderDomainService;
import org.food.ordering.domain.entity.Order;
import org.food.ordering.domain.entity.Restaurant;
import org.food.ordering.domain.event.OrderCreatedEvent;
import org.food.ordering.domain.exception.BadArgumentException;
import org.food.ordering.domain.exception.OrderDomainException;
import org.food.ordering.order.service.domain.dto.create.CreateOrderCommand;
import org.food.ordering.order.service.domain.mapper.OrderDataMapper;
import org.food.ordering.order.service.domain.port.output.repository.CustomerRepository;
import org.food.ordering.order.service.domain.port.output.repository.OrderRepository;
import org.food.ordering.order.service.domain.port.output.repository.RestaurantRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Slf4j
public class OrderCreateHelper {

    private final OrderDataMapper orderDataMapper;
    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final CustomerRepository customerRepository;

    public OrderCreateHelper(OrderDataMapper orderDataMapper,
                             OrderDomainService orderDomainService,
                             OrderRepository orderRepository,
                             RestaurantRepository restaurantRepository,
                             CustomerRepository customerRepository) {
        this.orderDataMapper = orderDataMapper;
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public OrderCreatedEvent persistOrder(CreateOrderCommand createOrderCommand) {
        checkCustomer(createOrderCommand.getCustomerId());
        final Restaurant restaurant = checkRestaurant(createOrderCommand);
        final Order order = orderDataMapper.createOrderFromOrderCommand(createOrderCommand);
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, restaurant);
        saveOrder(order);
        log.info("Order is created with id: {}", orderCreatedEvent.getOrder().getId().getValue());
        return orderCreatedEvent;
    }

    private Order saveOrder(Order order) {
        Order result = orderRepository.save(order);
        if (result == null) {
            log.error("Could not save order");
            throw new OrderDomainException("Could not save order");
        }
        log.info("Order is saved with id: {}", result.getId().getValue());
        return result;
    }

    private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
        Restaurant restaurant = orderDataMapper.createRestaurantFromOrderCommand(createOrderCommand);
        return restaurantRepository.findRestaurantInformation(restaurant).orElseThrow(() -> {
            log.warn("Could not find restaurant with id: {}", createOrderCommand.getRestaurantId());
            return new OrderDomainException(String.format("Could not find restaurant with id: %s", createOrderCommand.getRestaurantId()));
        });
    }

    private void checkCustomer(UUID customerId) {
        if (customerId == null) {
            throw new BadArgumentException("Cannot process null customer ID");
        }
        customerRepository.findCustomer(customerId).orElseThrow(() -> {
            log.warn("Could not find customer with id: {}", customerId);
            return new OrderDomainException(String.format("Could not find customer with id: %s", customerId));
        });
    }
}
