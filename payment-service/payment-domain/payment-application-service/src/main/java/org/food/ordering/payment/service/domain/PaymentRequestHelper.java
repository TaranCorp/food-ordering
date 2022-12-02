package org.food.ordering.payment.service.domain;

import org.food.ordering.domain.valueobject.CustomerId;
import org.food.ordering.payment.service.domain.dto.PaymentRequest;
import org.food.ordering.payment.service.domain.entity.CreditEntry;
import org.food.ordering.payment.service.domain.entity.CreditHistory;
import org.food.ordering.payment.service.domain.entity.Payment;
import org.food.ordering.payment.service.domain.event.PaymentEvent;
import org.food.ordering.payment.service.domain.exception.PaymentApplicationServiceException;
import org.food.ordering.payment.service.domain.mapper.PaymentDataMapper;
import org.food.ordering.payment.service.domain.ports.output.repository.CreditEntryRepository;
import org.food.ordering.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import org.food.ordering.payment.service.domain.ports.output.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
class PaymentRequestHelper {
    private static final Logger log = LoggerFactory.getLogger(PaymentRequestHelper.class);

    private final PaymentDomainService paymentDomainService;
    private final PaymentDataMapper paymentDataMapper;
    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final CreditHistoryRepository creditHistoryRepository;

    PaymentRequestHelper(PaymentDomainService paymentDomainService,
                         PaymentDataMapper paymentDataMapper,
                         PaymentRepository paymentRepository,
                         CreditEntryRepository creditEntryRepository,
                         CreditHistoryRepository creditHistoryRepository) {
        this.paymentDomainService = paymentDomainService;
        this.paymentDataMapper = paymentDataMapper;
        this.paymentRepository = paymentRepository;
        this.creditEntryRepository = creditEntryRepository;
        this.creditHistoryRepository = creditHistoryRepository;
    }

    @Transactional
    public PaymentEvent persistPayment(PaymentRequest paymentRequest) {
        log.info("Received payment complete event for order id: {}", paymentRequest.getOrderId());
        final Payment payment = paymentDataMapper.createPaymentFromPaymentRequest(paymentRequest);
        return createPaymentEvent(payment);
    }

    private PaymentEvent createPaymentEvent(Payment payment) {
        final CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        final List<CreditHistory> creditHistory = getCreditHistory(payment.getCustomerId());
        final List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentDomainService.validateAndInitiatePayment(payment, creditEntry, creditHistory, failureMessages);
        persistDbObject(payment, creditEntry, creditHistory, failureMessages);
        return paymentEvent;
    }

    private CreditEntry getCreditEntry(CustomerId customerId) {
        return creditEntryRepository.findByCustomerId(customerId)
                .orElseThrow(() -> {
                    String error = "Credit history for user with id: %s could not be found".formatted(customerId.getValue());
                    log.error(error);
                    return new PaymentApplicationServiceException(error);
                });
    }

    private List<CreditHistory> getCreditHistory(CustomerId customerId) {
        return creditHistoryRepository.findByCustomerId(customerId)
                .orElseThrow(() -> {
                    String error = "Could not find credit history for customer with id: %s".formatted(customerId.getValue());
                    log.error(error);
                    return new PaymentApplicationServiceException(error);
                });
    }

    private void persistDbObject(Payment payment, CreditEntry creditEntry, List<CreditHistory> creditHistory, List<String> failureMessages) {
        paymentRepository.save(payment);
        if (failureMessages.isEmpty()) {
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(getLastCreditHistory(creditHistory));
        }
    }

    private CreditHistory getLastCreditHistory(List<CreditHistory> creditHistory) {
        return creditHistory.get(creditHistory.size() - 1);
    }

    @Transactional
    public PaymentEvent persistCancelPayment(PaymentRequest paymentRequest) {
        log.info("Received payment rollback event for order id: {}", paymentRequest.getOrderId());
        Payment payment = getPaymentResponse(paymentRequest)
                .orElseThrow(() -> {
                    String error = "Payment could not be found for order id: %s".formatted(paymentRequest.getOrderId());
                    log.error(error);
                    return new PaymentApplicationServiceException(error);
                });
        return createPaymentEvent(payment);
    }

    private Optional<Payment> getPaymentResponse(PaymentRequest paymentRequest) {
        return paymentRepository.findByOrderId(UUID.fromString(paymentRequest.getOrderId()));
    }
}
