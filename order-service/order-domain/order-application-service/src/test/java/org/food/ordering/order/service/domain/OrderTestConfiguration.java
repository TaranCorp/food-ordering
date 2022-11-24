package org.food.ordering.order.service.domain;

import org.food.ordering.domain.OrderDomainService;
import org.food.ordering.domain.OrderDomainServiceImpl;
import org.food.ordering.order.service.domain.port.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import org.food.ordering.order.service.domain.port.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import org.food.ordering.order.service.domain.port.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import org.food.ordering.order.service.domain.port.output.repository.CustomerRepository;
import org.food.ordering.order.service.domain.port.output.repository.OrderRepository;
import org.food.ordering.order.service.domain.port.output.repository.RestaurantRepository;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@SpringBootApplication(scanBasePackages = "org.food.ordering")
public class OrderTestConfiguration {

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }

    @Bean
    OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher() {
        return mock(OrderCreatedPaymentRequestMessagePublisher.class);
    }

    @Bean
    OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher() {
        return mock(OrderCancelledPaymentRequestMessagePublisher.class);
    }

    @Bean
    OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher() {
        return mock(OrderPaidRestaurantRequestMessagePublisher.class);
    }

    @Bean
    OrderRepository orderRepository() {
        return mock(OrderRepository.class);
    }

    @Bean
    CustomerRepository customerRepository() {
        return mock(CustomerRepository.class);
    }

    @Bean
    RestaurantRepository restaurantRepository() {
        return mock(RestaurantRepository.class);
    }
}
