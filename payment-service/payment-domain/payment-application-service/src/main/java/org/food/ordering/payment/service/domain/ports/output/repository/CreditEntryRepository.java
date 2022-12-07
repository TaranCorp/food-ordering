package org.food.ordering.payment.service.domain.ports.output.repository;

import org.food.ordering.payment.service.domain.entity.CreditEntry;

import java.util.Optional;
import java.util.UUID;

public interface CreditEntryRepository {
    CreditEntry save(CreditEntry creditEntry);

    Optional<CreditEntry> findByCustomerId(UUID customerId);
}
