package org.food.ordering.order.service.dataaccess.customer.adapter;

import org.food.ordering.domain.entity.Customer;
import org.food.ordering.order.service.dataaccess.customer.mapper.CustomerDataAccessMapper;
import org.food.ordering.order.service.dataaccess.customer.repository.CustomerJpaRepository;
import org.food.ordering.order.service.domain.port.output.repository.CustomerRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository repository;
    private final CustomerDataAccessMapper mapper;

    public CustomerRepositoryImpl(CustomerJpaRepository repository, CustomerDataAccessMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        return repository.findById(customerId)
                .map(mapper::customerFromCustomerEntity);
    }
}
