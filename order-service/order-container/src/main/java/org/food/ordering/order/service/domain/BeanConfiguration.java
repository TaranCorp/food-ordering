package org.food.ordering.order.service.domain;

import org.food.ordering.domain.OrderDomainService;
import org.food.ordering.domain.OrderDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class BeanConfiguration {
    @Bean
    OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }
}
