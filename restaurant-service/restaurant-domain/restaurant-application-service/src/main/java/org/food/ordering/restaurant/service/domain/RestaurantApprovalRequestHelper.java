package org.food.ordering.restaurant.service.domain;

import org.food.ordering.domain.entity.BaseEntity;
import org.food.ordering.domain.valueobject.OrderId;
import org.food.ordering.domain.valueobject.ProductId;
import org.food.ordering.restaurant.service.domain.dto.RestaurantApprovalRequest;
import org.food.ordering.restaurant.service.domain.entity.Product;
import org.food.ordering.restaurant.service.domain.entity.Restaurant;
import org.food.ordering.restaurant.service.domain.event.OrderApprovalEvent;
import org.food.ordering.restaurant.service.domain.event.OrderApprovedEvent;
import org.food.ordering.restaurant.service.domain.event.OrderRejectedEvent;
import org.food.ordering.restaurant.service.domain.exception.RestaurantNotFoundException;
import org.food.ordering.restaurant.service.domain.mapper.RestaurantDataMapper;
import org.food.ordering.restaurant.service.domain.port.output.message.publisher.OrderApprovedMessagePublisher;
import org.food.ordering.restaurant.service.domain.port.output.message.publisher.OrderRejectedMessagePublisher;
import org.food.ordering.restaurant.service.domain.port.output.repository.OrderApprovalRepository;
import org.food.ordering.restaurant.service.domain.port.output.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class RestaurantApprovalRequestHelper {
    private static final Logger log = LoggerFactory.getLogger(RestaurantApprovalRequestHelper.class);

    private final RestaurantDomainService restaurantDomainService;
    private final RestaurantDataMapper restaurantDataMapper;
    private final RestaurantRepository restaurantRepository;
    private final OrderApprovalRepository orderApprovalRepository;
    private final OrderApprovedMessagePublisher orderApprovedMessagePublisher;
    private final OrderRejectedMessagePublisher orderRejectedMessagePublisher;

    public RestaurantApprovalRequestHelper(RestaurantDomainService restaurantDomainService,
                                           RestaurantDataMapper restaurantDataMapper,
                                           RestaurantRepository restaurantRepository,
                                           OrderApprovalRepository orderApprovalRepository,
                                           OrderApprovedMessagePublisher orderApprovedMessagePublisher,
                                           OrderRejectedMessagePublisher orderRejectedMessagePublisher) {
        this.restaurantDomainService = restaurantDomainService;
        this.restaurantDataMapper = restaurantDataMapper;
        this.restaurantRepository = restaurantRepository;
        this.orderApprovalRepository = orderApprovalRepository;
        this.orderApprovedMessagePublisher = orderApprovedMessagePublisher;
        this.orderRejectedMessagePublisher = orderRejectedMessagePublisher;
    }

    @Transactional
    public OrderApprovalEvent persistOrderApproval(RestaurantApprovalRequest restaurantApprovalRequest) {
        log.info("Processing RestaurantApprovalRequest for order id: {}", restaurantApprovalRequest.getOrderId());
        final ArrayList<String> failureMessages = new ArrayList<>();
        final Restaurant restaurant = findRestaurant(restaurantApprovalRequest);
        final OrderApprovalEvent orderApprovalEvent = restaurantDomainService.validateOrder(restaurant, failureMessages);
        orderApprovalRepository.save(restaurant.getOrderApproval());
        return failureMessages.isEmpty()
                    ? getOrderApprovedEvent(orderApprovalEvent)
                    : getOrderRejectedEvent(orderApprovalEvent);
    }

    private OrderRejectedEvent getOrderRejectedEvent(OrderApprovalEvent orderApprovalEvent) {
        return new OrderRejectedEvent(
                orderApprovalEvent.getOrderApproval(),
                orderApprovalEvent.getRestaurantId(),
                orderApprovalEvent.getFailureMessages(),
                orderApprovalEvent.getCreatedAt()
        );
    }

    private OrderApprovedEvent getOrderApprovedEvent(OrderApprovalEvent orderApprovalEvent) {
        return new OrderApprovedEvent(
                orderApprovalEvent.getOrderApproval(),
                orderApprovalEvent.getRestaurantId(),
                orderApprovalEvent.getCreatedAt()
        );
    }

    private Restaurant findRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {
        final Restaurant restaurant = restaurantDataMapper.createRestaurantFromRestaurantApprovalRequest(restaurantApprovalRequest);
        return restaurantRepository.findRestaurant(restaurant)
                .map(persistedRestaurant -> {
                    persistedRestaurant.setActive(persistedRestaurant.isActive());
                    updateNotPersistedProducts(restaurant, persistedRestaurant);
                    restaurant.getOrderDetail().setId(new OrderId(UUID.fromString(restaurantApprovalRequest.getOrderId())));
                    return persistedRestaurant;
                })
                .orElseThrow(restaurantNotFoundErrorHandler(restaurantApprovalRequest));
    }

    private Supplier<RestaurantNotFoundException> restaurantNotFoundErrorHandler(RestaurantApprovalRequest restaurantApprovalRequest) {
        return () -> {
            String error = "Cannot find restaurant with id: %s for order id: {}"
                    .formatted(restaurantApprovalRequest.getRestaurantId(), restaurantApprovalRequest.getOrderId());
            log.error(error);
            return new RestaurantNotFoundException(error);
        };
    }

    private void updateNotPersistedProducts(Restaurant restaurant, Restaurant persistedRestaurant) {
        final Map<ProductId, Product> productsMap = persistedRestaurant.getOrderDetail().getProducts().stream()
                .collect(Collectors.toMap(
                        BaseEntity::getId,
                        Function.identity()
                ));

        restaurant.getOrderDetail().getProducts().forEach(product -> {
            Product persistedProduct = productsMap.get(product.getId());
            if (persistedProduct != null) {
                product.updateWithConfirmedNamePriceAndAvailability(
                        persistedProduct.getName(),
                        persistedProduct.getPrice(),
                        persistedProduct.isAvailable());
            }
        });
    }
}
