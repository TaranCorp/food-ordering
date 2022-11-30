package org.food.ordering.order.service.dataaccess.customer.repository;

import org.food.ordering.order.service.dataaccess.customer.entity.CustomerEntity;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerJpaRepository extends Repository<CustomerEntity, UUID> {
    Optional<CustomerEntity> findById(UUID customerId);
}
