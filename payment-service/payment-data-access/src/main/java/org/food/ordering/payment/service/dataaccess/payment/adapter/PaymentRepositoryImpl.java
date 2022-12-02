package org.food.ordering.payment.service.dataaccess.payment.adapter;

import org.food.ordering.payment.service.dataaccess.payment.mapper.PaymentDataAccessMapper;
import org.food.ordering.payment.service.dataaccess.payment.repository.PaymentJpaRepository;
import org.food.ordering.payment.service.domain.entity.Payment;
import org.food.ordering.payment.service.domain.ports.output.repository.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentDataAccessMapper paymentDataAccessMapper;

    public PaymentRepositoryImpl(PaymentJpaRepository paymentJpaRepository, PaymentDataAccessMapper paymentDataAccessMapper) {
        this.paymentJpaRepository = paymentJpaRepository;
        this.paymentDataAccessMapper = paymentDataAccessMapper;
    }

    @Override
    public Payment save(Payment payment) {
        return paymentDataAccessMapper.createPaymentFromPaymentEntity(
                paymentJpaRepository.save(
                        paymentDataAccessMapper.createPaymentEntityFromPayment(payment)
                )
        );
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return paymentJpaRepository.findByOrderId(orderId)
                .map(paymentDataAccessMapper::createPaymentFromPaymentEntity);
    }
}
