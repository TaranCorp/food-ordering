package org.food.ordering.order.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.food.ordering.domain.OrderDomainService;
import org.food.ordering.domain.entity.Order;
import org.food.ordering.domain.entity.Restaurant;
import org.food.ordering.domain.event.OrderCreatedEvent;
import org.food.ordering.domain.exception.BadArgumentException;
import org.food.ordering.domain.exception.OrderDomainException;
import org.food.ordering.order.service.domain.dto.create.CreateOrderCommand;
import org.food.ordering.order.service.domain.dto.create.CreateOrderResponse;
import org.food.ordering.order.service.domain.mapper.OrderDataMapper;
import org.food.ordering.order.service.domain.port.output.repository.CustomerRepository;
import org.food.ordering.order.service.domain.port.output.repository.OrderRepository;
import org.food.ordering.order.service.domain.port.output.repository.RestaurantRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
class OrderCreateCommandHandler {

    private final OrderDomainService orderDomainService;

    private final OrderRepository orderRepository;

    private final CustomerRepository customerRepository;

    private final RestaurantRepository restaurantRepository;

    private final OrderDataMapper orderDataMapper;

    OrderCreateCommandHandler(OrderDomainService orderDomainService,
                              OrderRepository orderRepository,
                              CustomerRepository customerRepository,
                              RestaurantRepository restaurantRepository,
                              OrderDataMapper orderDataMapper) {
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderDataMapper = orderDataMapper;
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        checkCustomer(createOrderCommand.getCustomerId());
        final Restaurant restaurant = checkRestaurant(createOrderCommand);
        final Order order = orderDataMapper.createOrderFromOrderCommand(createOrderCommand);
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, restaurant); // FIXME fire event

        Order orderResult = saveOrder(order);
        log.info("Order is created with id: {}", orderResult.getId().getValue());
        return orderDataMapper.createOrderResponseFromOrder(orderResult);
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
