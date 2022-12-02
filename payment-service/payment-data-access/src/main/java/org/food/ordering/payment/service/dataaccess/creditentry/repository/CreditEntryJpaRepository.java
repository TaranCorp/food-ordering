package org.food.ordering.payment.service.dataaccess.creditentry.repository;

import org.food.ordering.domain.valueobject.CustomerId;
import org.food.ordering.payment.service.dataaccess.creditentry.entity.CreditEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CreditEntryJpaRepository extends JpaRepository<CreditEntryEntity, UUID> {
    Optional<CreditEntryEntity> findByCustomerId(CustomerId customerId);
}
