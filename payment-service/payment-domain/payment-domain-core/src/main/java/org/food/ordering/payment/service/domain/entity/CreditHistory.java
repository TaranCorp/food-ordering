package org.food.ordering.payment.service.domain.entity;

import org.food.ordering.domain.entity.BaseEntity;
import org.food.ordering.domain.valueobject.CustomerId;
import org.food.ordering.domain.valueobject.Money;
import org.food.ordering.payment.service.domain.valueobject.CreditHistoryId;
import org.food.ordering.payment.service.domain.valueobject.TransactionType;

import java.util.UUID;

public class CreditHistory extends BaseEntity<CreditHistoryId> {
    private final CustomerId customerId;
    private final Money amount;
    private final TransactionType transactionType;

    public static CreditHistory createCreditHistory(CustomerId customerId, Money amount, TransactionType transactionType) {
        return new CreditHistory(
                new CreditHistoryId(UUID.randomUUID()),
                customerId,
                amount,
                transactionType
        );
    }

    public CreditHistory(CreditHistoryId creditHistoryId, CustomerId customerId, Money amount, TransactionType transactionType) {
        super.setId(creditHistoryId);
        this.customerId = customerId;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Money getAmount() {
        return amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }
}
