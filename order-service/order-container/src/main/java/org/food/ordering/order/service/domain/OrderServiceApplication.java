package org.food.ordering.order.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {
        "org.food.ordering.order.service.dataaccess",
        "org.food.ordering.dataaccess"
})
@EntityScan(basePackages = {
        "org.food.ordering.order.service.dataaccess",
        "org.food.ordering.dataaccess"
})
@SpringBootApplication(scanBasePackages = "org.food.ordering")
class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
