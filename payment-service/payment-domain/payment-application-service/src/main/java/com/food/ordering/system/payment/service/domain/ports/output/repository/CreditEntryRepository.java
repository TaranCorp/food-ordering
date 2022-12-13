package org.food.ordering.payment.service.domain.ports.output.repository;

import org.food.ordering.domain.valueobject.CustomerId;
import org.food.ordering.payment.service.domain.entity.CreditEntry;

import java.util.Optional;

public interface CreditEntryRepository {

    CreditEntry save(CreditEntry creditEntry);

    Optional<CreditEntry> findByCustomerId(CustomerId customerId);
}
