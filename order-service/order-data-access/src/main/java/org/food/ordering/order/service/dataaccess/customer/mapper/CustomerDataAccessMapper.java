package org.food.ordering.order.service.dataaccess.customer.mapper;

import org.food.ordering.domain.entity.Customer;
import org.food.ordering.order.service.dataaccess.customer.entity.CustomerEntity;
import org.springframework.stereotype.Component;

import static org.food.ordering.domain.entity.Customer.createCustomerById;

@Component
public class CustomerDataAccessMapper {
    public Customer customerFromCustomerEntity(CustomerEntity customerEntity) {
        return createCustomerById(customerEntity.getId());
    }
}
