package org.food.ordering.order.service.domain.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "order-service")
public class OrderServiceConfigData {
    private String paymentRequestTopicName;
    private String paymentResponseTopicName;
    private String restaurantApprovalRequestTopicName;
    private String restaurantApprovalResponseTopicName;

    public String getPaymentRequestTopicName() {
        return paymentRequestTopicName;
    }

    public void setPaymentRequestTopicName(String paymentRequestTopicName) {
        this.paymentRequestTopicName = paymentRequestTopicName;
    }

    public String getPaymentResponseTopicName() {
        return paymentResponseTopicName;
    }

    public void setPaymentResponseTopicName(String paymentResponseTopicName) {
        this.paymentResponseTopicName = paymentResponseTopicName;
    }

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
