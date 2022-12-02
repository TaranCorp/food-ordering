package org.food.ordering.payment.service.dataaccess.creditentry.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "credit_entry")
public class CreditEntryEntity {

    @Id
    private UUID id;
    private UUID customerId;
    private BigDecimal totalCreditAmount;

    public CreditEntryEntity() {
    }

    public CreditEntryEntity(UUID id, UUID customerId, BigDecimal totalCreditAmount) {
        this.id = id;
        this.customerId = customerId;
        this.totalCreditAmount = totalCreditAmount;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public BigDecimal getTotalCreditAmount() {
        return totalCreditAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditEntryEntity that = (CreditEntryEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
