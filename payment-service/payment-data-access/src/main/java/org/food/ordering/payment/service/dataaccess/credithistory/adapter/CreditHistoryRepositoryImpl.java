package org.food.ordering.payment.service.dataaccess.credithistory.adapter;

import org.food.ordering.domain.valueobject.CustomerId;
import org.food.ordering.payment.service.dataaccess.credithistory.mapper.CreditHistoryDataAccessMapper;
import org.food.ordering.payment.service.dataaccess.credithistory.repository.CreditHistoryJpaRepository;
import org.food.ordering.payment.service.domain.entity.CreditHistory;
import org.food.ordering.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CreditHistoryRepositoryImpl implements CreditHistoryRepository {

    private final CreditHistoryJpaRepository repository;
    private final CreditHistoryDataAccessMapper mapper;

    public CreditHistoryRepositoryImpl(CreditHistoryJpaRepository repository, CreditHistoryDataAccessMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public CreditHistory save(CreditHistory creditHistory) {
        return mapper.creditHistoryFromCreditHistoryEntity(
                repository.save(
                        mapper.creditHistoryEntityFromCreditHistory(creditHistory)
                )
        );
    }

    @Override
    public Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId) {
        return repository.findByCustomerId(customerId.getValue())
                .map(mapper::creditHistoryFromCreditHistoryEntities);
    }
}
