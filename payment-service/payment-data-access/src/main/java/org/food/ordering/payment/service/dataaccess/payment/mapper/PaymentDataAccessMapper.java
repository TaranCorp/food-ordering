package org.food.ordering.payment.service.dataaccess.payment.mapper;

import org.food.ordering.domain.valueobject.CustomerId;
import org.food.ordering.domain.valueobject.Money;
import org.food.ordering.domain.valueobject.OrderId;
import org.food.ordering.payment.service.dataaccess.payment.entity.PaymentEntity;
import org.food.ordering.payment.service.domain.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentDataAccessMapper {

    public PaymentEntity createPaymentEntityFromPayment(Payment payment) {
        return new PaymentEntity(
                payment.getId().getValue(),
                payment.getCustomerId().getValue(),
                payment.getOrderId().getValue(),
                payment.getPrice().getAmount(),
                payment.getPaymentStatus(),
                payment.getCreatedAt()
        );
    }

    public Payment createPaymentFromPaymentEntity(PaymentEntity payment) {
        return new Payment(
                new OrderId(payment.getOrderId()),
                new CustomerId(payment.getCustomerId()),
                new Money(payment.getPrice()),
                payment.getPaymentStatus(),
                payment.getCreatedAt()
        );
    }
}
