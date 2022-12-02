package org.food.ordering.payment.service.dataaccess.credithistory.mapper;

import org.food.ordering.domain.valueobject.CustomerId;
import org.food.ordering.domain.valueobject.Money;
import org.food.ordering.payment.service.dataaccess.credithistory.entity.CreditHistoryEntity;
import org.food.ordering.payment.service.domain.entity.CreditHistory;
import org.food.ordering.payment.service.domain.valueobject.CreditHistoryId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreditHistoryDataAccessMapper {
    public CreditHistoryEntity creditHistoryEntityFromCreditHistory(CreditHistory creditHistory) {
        return new CreditHistoryEntity(
                creditHistory.getId().getValue(),
                creditHistory.getCustomerId().getValue(),
                creditHistory.getAmount().getAmount(),
                creditHistory.getTransactionType()
        );
    }

    public List<CreditHistory> creditHistoryFromCreditHistoryEntities(List<CreditHistoryEntity> creditHistoryEntities) {
        return creditHistoryEntities.stream()
                .map(this::creditHistoryFromCreditHistoryEntity)
                .toList();
    }

    public CreditHistory creditHistoryFromCreditHistoryEntity(CreditHistoryEntity entity) {
        return new CreditHistory(
                new CreditHistoryId(entity.getId()),
                new CustomerId(entity.getCustomerId()),
                new Money(entity.getAmount()),
                entity.getTransactionType()
        );
    }
}
