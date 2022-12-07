package org.food.ordering.payment.service.dataaccess.credithistory.entity;

import org.food.ordering.payment.service.domain.valueobject.TransactionType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "credit_history")
public class CreditHistoryEntity {

    @Id
    private UUID id;
    private UUID customerId;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TransactionType transactionType;

    public CreditHistoryEntity() {
    }

    public CreditHistoryEntity(UUID id, UUID customerId, BigDecimal amount, TransactionType transactionType) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditHistoryEntity that = (CreditHistoryEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
