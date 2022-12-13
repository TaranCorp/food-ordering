package org.food.ordering.payment.service.domain.valueobject;

import org.food.ordering.domain.valueobject.BaseId;

import java.util.UUID;

public class PaymentId extends BaseId<UUID> {
    public PaymentId(UUID value) {
        super(value);
    }
}
