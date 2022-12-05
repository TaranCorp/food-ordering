package org.food.ordering.restaurant.service.dataaccess.restaurant.adapter;

import org.food.ordering.restaurant.service.dataaccess.restaurant.mapper.RestaurantDataMapper;
import org.food.ordering.restaurant.service.dataaccess.restaurant.repository.OrderApprovalJpaRepository;
import org.food.ordering.restaurant.service.domain.entity.OrderApproval;
import org.food.ordering.restaurant.service.domain.port.output.repository.OrderApprovalRepository;
import org.springframework.stereotype.Repository;

@Repository
public class OrderApprovalRepositoryImpl implements OrderApprovalRepository {

    private final OrderApprovalJpaRepository orderApprovalJpaRepository;
    private final RestaurantDataMapper restaurantDataMapper;

    public OrderApprovalRepositoryImpl(OrderApprovalJpaRepository orderApprovalJpaRepository,
                                       RestaurantDataMapper restaurantDataMapper) {
        this.orderApprovalJpaRepository = orderApprovalJpaRepository;
        this.restaurantDataMapper = restaurantDataMapper;
    }

    @Override
    public OrderApproval save(OrderApproval orderApproval) {
        return restaurantDataMapper.orderApprovalFromOrderApprovalEntity(orderApprovalJpaRepository.save(
                restaurantDataMapper.orderApprovalEntityFromOrderApproval(orderApproval)
        ));
    }
}
