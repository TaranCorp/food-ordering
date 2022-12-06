package org.food.ordering.restaurant.service.domain.config;

import org.food.ordering.restaurant.service.domain.RestaurantDomainService;
import org.food.ordering.restaurant.service.domain.RestaurantDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RestaurantBeanConfig {
    @Bean
    RestaurantDomainService restaurantDomainService() {
        return new RestaurantDomainServiceImpl();
    }
}
