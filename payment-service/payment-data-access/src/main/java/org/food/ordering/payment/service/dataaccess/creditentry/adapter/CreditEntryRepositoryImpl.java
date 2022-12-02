package org.food.ordering.payment.service.dataaccess.creditentry.adapter;

import org.food.ordering.domain.valueobject.CustomerId;
import org.food.ordering.payment.service.dataaccess.creditentry.mapper.CreditEntryDataAccessMapper;
import org.food.ordering.payment.service.dataaccess.creditentry.repository.CreditEntryJpaRepository;
import org.food.ordering.payment.service.domain.entity.CreditEntry;
import org.food.ordering.payment.service.domain.ports.output.repository.CreditEntryRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CreditEntryRepositoryImpl implements CreditEntryRepository {

    private final CreditEntryJpaRepository repository;
    private final CreditEntryDataAccessMapper mapper;

    public CreditEntryRepositoryImpl(CreditEntryJpaRepository repository, CreditEntryDataAccessMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public CreditEntry save(CreditEntry creditEntry) {
        return mapper.creditEntryFromCreditEntryEntity(
                repository.save(
                        mapper.creditEntryEntityFromCreditEntry(creditEntry)
                )
        );
    }

    @Override
    public Optional<CreditEntry> findByCustomerId(CustomerId customerId) {
        return repository.findByCustomerId(customerId)
                .map(mapper::creditEntryFromCreditEntryEntity);
    }
}
