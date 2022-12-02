package org.food.ordering.payment.service.dataaccess.creditentry.mapper;

import org.food.ordering.domain.valueobject.CustomerId;
import org.food.ordering.domain.valueobject.Money;
import org.food.ordering.payment.service.dataaccess.creditentry.entity.CreditEntryEntity;
import org.food.ordering.payment.service.domain.entity.CreditEntry;
import org.food.ordering.payment.service.domain.valueobject.CreditEntryId;
import org.springframework.stereotype.Component;

@Component
public class CreditEntryDataAccessMapper {
    public CreditEntryEntity creditEntryEntityFromCreditEntry(CreditEntry creditEntry) {
        return new CreditEntryEntity(
                creditEntry.getId().getValue(),
                creditEntry.getCustomerId().getValue(),
                creditEntry.getTotalCreditAmount().getAmount()
        );
    }

    public CreditEntry creditEntryFromCreditEntryEntity(CreditEntryEntity entity) {
        return new CreditEntry(
                new CreditEntryId(entity.getId()),
                new CustomerId(entity.getCustomerId()),
                new Money(entity.getTotalCreditAmount())
        );
    }
}
