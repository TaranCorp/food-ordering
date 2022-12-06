package org.food.ordering.restaurant.service.messaging.mapper;

import org.food.ordering.domain.valueobject.ProductId;
import org.food.ordering.domain.valueobject.RestaurantOrderStatus;
import org.food.ordering.kafka.order.avro.model.OrderApprovalStatus;
import org.food.ordering.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import org.food.ordering.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import org.food.ordering.restaurant.service.domain.dto.RestaurantApprovalRequest;
import org.food.ordering.restaurant.service.domain.entity.Product;
import org.food.ordering.restaurant.service.domain.event.OrderApprovedEvent;
import org.food.ordering.restaurant.service.domain.event.OrderRejectedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RestaurantMessageMapper {
    public RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModelFromOrderApprovedEvent(OrderApprovedEvent domainEvent) {
        return RestaurantApprovalResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setCreatedAt(domainEvent.getCreatedAt().toInstant())
                .setOrderId(domainEvent.getOrderApproval().getOrderId().getValue().toString())
                .setRestaurantId(domainEvent.getRestaurantId().toString())
                .setOrderApprovalStatus(OrderApprovalStatus.valueOf(domainEvent.getOrderApproval().getApprovalStatus().name()))
                .setFailureMessages(domainEvent.getFailureMessages())
                .build();
    }

    public RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModelFromOrderRejectedEvent(OrderRejectedEvent domainEvent) {
        return RestaurantApprovalResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setCreatedAt(domainEvent.getCreatedAt().toInstant())
                .setOrderId(domainEvent.getOrderApproval().getOrderId().getValue().toString())
                .setRestaurantId(domainEvent.getRestaurantId().toString())
                .setOrderApprovalStatus(OrderApprovalStatus.valueOf(domainEvent.getOrderApproval().getApprovalStatus().name()))
                .setFailureMessages(domainEvent.getFailureMessages())
                .build();
    }

    public RestaurantApprovalRequest restaurantApprovalRequestFromRestaurantApprovalRequestAvroModel(RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel) {
        return new RestaurantApprovalRequest(
                restaurantApprovalRequestAvroModel.getId(),
                restaurantApprovalRequestAvroModel.getSagaId(),
                restaurantApprovalRequestAvroModel.getRestaurantId(),
                restaurantApprovalRequestAvroModel.getOrderId(),
                RestaurantOrderStatus.valueOf(restaurantApprovalRequestAvroModel.getRestaurantOrderStatus().name()),
                mapProducts(restaurantApprovalRequestAvroModel),
                restaurantApprovalRequestAvroModel.getPrice(),
                restaurantApprovalRequestAvroModel.getCreatedAt()
        );
    }

    private List<Product> mapProducts(RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel) {
        return restaurantApprovalRequestAvroModel.getProducts().stream()
                .map(product -> new Product(
                        new ProductId(UUID.fromString(product.getId())),
                        product.getQuantity()
                ))
                .toList();
    }
}
