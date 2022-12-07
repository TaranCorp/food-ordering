package org.food.ordering.payment.service.dataaccess.credithistory.repository;

import org.food.ordering.payment.service.dataaccess.credithistory.entity.CreditHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditHistoryJpaRepository extends JpaRepository<CreditHistoryEntity, UUID> {
    Optional<List<CreditHistoryEntity>> findByCustomerId(UUID customerId);
}
