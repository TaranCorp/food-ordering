package org.food.ordering.payment.service.domain.config;

import org.food.ordering.payment.service.domain.PaymentDomainService;
import org.food.ordering.payment.service.domain.PaymentDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class BeanConfiguration {

    @Bean
    PaymentDomainService paymentDomainService() {
        return new PaymentDomainServiceImpl();
    }

}
