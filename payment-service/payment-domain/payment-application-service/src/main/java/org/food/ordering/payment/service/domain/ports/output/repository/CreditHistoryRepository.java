package org.food.ordering.payment.service.domain.ports.output.repository;

import org.food.ordering.domain.valueobject.CustomerId;
import org.food.ordering.payment.service.domain.entity.CreditHistory;

import java.util.List;
import java.util.Optional;

public interface CreditHistoryRepository {
    CreditHistory save(CreditHistory creditHistory);

    Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId);
}
