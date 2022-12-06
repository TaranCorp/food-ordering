package org.food.ordering.restaurant.service.dataaccess.restaurant.adapter;

import org.food.ordering.restaurant.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import org.food.ordering.restaurant.service.dataaccess.restaurant.repository.OrderApprovalJpaRepository;
import org.food.ordering.restaurant.service.domain.entity.OrderApproval;
import org.food.ordering.restaurant.service.domain.port.output.repository.OrderApprovalRepository;
import org.springframework.stereotype.Repository;

@Repository
public class OrderApprovalRepositoryImpl implements OrderApprovalRepository {

    private final OrderApprovalJpaRepository orderApprovalJpaRepository;
    private final RestaurantDataAccessMapper restaurantDataAccessMapper;

    public OrderApprovalRepositoryImpl(OrderApprovalJpaRepository orderApprovalJpaRepository,
                                       RestaurantDataAccessMapper restaurantDataAccessMapper) {
        this.orderApprovalJpaRepository = orderApprovalJpaRepository;
        this.restaurantDataAccessMapper = restaurantDataAccessMapper;
    }

    @Override
    public OrderApproval save(OrderApproval orderApproval) {
        return restaurantDataAccessMapper.orderApprovalFromOrderApprovalEntity(orderApprovalJpaRepository.save(
                restaurantDataAccessMapper.orderApprovalEntityFromOrderApproval(orderApproval)
        ));
    }
}
