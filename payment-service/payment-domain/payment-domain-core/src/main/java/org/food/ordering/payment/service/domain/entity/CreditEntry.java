package org.food.ordering.payment.service.domain.entity;

import org.food.ordering.domain.entity.BaseEntity;
import org.food.ordering.domain.valueobject.CustomerId;
import org.food.ordering.domain.valueobject.Money;
import org.food.ordering.payment.service.domain.valueobject.CreditEntryId;

public class CreditEntry extends BaseEntity<CreditEntryId> {
    private final CustomerId customerId;
    private Money totalCreditAmount;

    public CreditEntry(CreditEntryId id, CustomerId customerId, Money totalCreditAmount) {
        super.setId(id);
        this.customerId = customerId;
        this.totalCreditAmount = totalCreditAmount;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Money getTotalCreditAmount() {
        return totalCreditAmount;
    }

    public void addAmount(Money amount) {
        totalCreditAmount.add(amount);
    }

    public void subtractAmount(Money amount) {
        totalCreditAmount = totalCreditAmount.subtract(amount);
    }
}
