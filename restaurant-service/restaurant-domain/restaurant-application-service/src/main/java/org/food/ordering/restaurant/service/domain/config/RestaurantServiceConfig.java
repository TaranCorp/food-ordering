package org.food.ordering.restaurant.service.domain.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "restaurant-service")
public class RestaurantServiceConfig {
    private String restaurantApprovalRequestTopicName;
    private String restaurantApprovalResponseTopicName;

    public String getRestaurantApprovalRequestTopicName() {
        return restaurantApprovalRequestTopicName;
    }

    public void setRestaurantApprovalRequestTopicName(String restaurantApprovalRequestTopicName) {
        this.restaurantApprovalRequestTopicName = restaurantApprovalRequestTopicName;
    }

    public String getRestaurantApprovalResponseTopicName() {
        return restaurantApprovalResponseTopicName;
    }

    public void setRestaurantApprovalResponseTopicName(String restaurantApprovalResponseTopicName) {
        this.restaurantApprovalResponseTopicName = restaurantApprovalResponseTopicName;
    }
}
