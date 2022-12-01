package org.food.ordering.payment.service.domain;

import org.food.ordering.domain.valueobject.Money;
import org.food.ordering.domain.valueobject.PaymentStatus;
import org.food.ordering.payment.service.domain.entity.CreditEntry;
import org.food.ordering.payment.service.domain.entity.CreditHistory;
import org.food.ordering.payment.service.domain.entity.Payment;
import org.food.ordering.payment.service.domain.event.PaymentCancelledEvent;
import org.food.ordering.payment.service.domain.event.PaymentCompletedEvent;
import org.food.ordering.payment.service.domain.event.PaymentEvent;
import org.food.ordering.payment.service.domain.event.PaymentFailedEvent;
import org.food.ordering.payment.service.domain.valueobject.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static org.food.ordering.payment.service.domain.entity.CreditHistory.createCreditHistory;

public class PaymentDomainServiceImpl implements PaymentDomainService {
    private static final Logger log = LoggerFactory.getLogger(PaymentDomainServiceImpl.class);

    @Override
    public PaymentEvent validateAndInitiatePayment(
            Payment payment,
            CreditEntry creditEntry,
            List<CreditHistory> creditHistory,
            List<String> failureMessages
    ) {
        payment.validatePayment(failureMessages);
        payment.initializePayment();
        validateCreditEntry(payment, creditEntry, failureMessages);
        subtractCreditEntry(payment, creditEntry);
        updateCreditHistory(payment, creditHistory, TransactionType.DEBIT);
        validateCreditHistory(creditEntry, creditHistory, failureMessages);

        if (failureMessages.isEmpty()) {
            log.info("Payment with id %s is completed for order id %s".formatted(payment.getId().getValue(), payment.getOrderId().getValue()));
            payment.updateStatus(PaymentStatus.COMPLETED);
            return new PaymentCompletedEvent(payment);
        }
        log.info("Payment initiation with id: %s failed for order id: %s".formatted(payment.getId().getValue(), payment.getOrderId().getValue()));
        payment.updateStatus(PaymentStatus.FAILED);
        return new PaymentFailedEvent(payment, failureMessages);
    }

    private void validateCreditHistory(CreditEntry creditEntry, List<CreditHistory> creditHistory, List<String> failureMessages) {
        final Money totalCreditHistory = collectMoneyOfType(TransactionType.CREDIT, creditHistory);
        final Money totalDebitHistory = collectMoneyOfType(TransactionType.DEBIT, creditHistory);

        if (totalDebitHistory.isGreaterThan(totalCreditHistory)) {
            String totalCreditErrorMsg = "Customer with id %s doesn't have enough credit according to credit history".formatted(creditEntry.getCustomerId().getValue());
            log.error(totalCreditErrorMsg);
            failureMessages.add(totalCreditErrorMsg);
        }

        if (!creditEntry.getTotalCreditAmount().equals(totalCreditHistory.subtract(totalDebitHistory))) {
            String entryCreditErrorMsg = "Credit history total is not equal to current credit for customer id: %s".formatted(creditEntry.getCustomerId().getValue());
            log.error(entryCreditErrorMsg);
            failureMessages.add(entryCreditErrorMsg);
        }
    }

    private Money collectMoneyOfType(TransactionType transactionType, List<CreditHistory> creditHistory) {
        return creditHistory.stream()
                .filter(credit -> credit.getTransactionType() == transactionType)
                .map(CreditHistory::getAmount)
                .reduce(Money.ZERO, Money::add);
    }

    private void updateCreditHistory(Payment payment, List<CreditHistory> creditHistory, TransactionType debit) {
        creditHistory.add(createCreditHistory(payment.getCustomerId(), payment.getPrice(), debit));
    }

    private void subtractCreditEntry(Payment payment, CreditEntry creditEntry) {
        creditEntry.subtractAmount(payment.getPrice());
    }

    private void validateCreditEntry(Payment payment, CreditEntry creditEntry, List<String> failureMessages) {
        if (payment.getPrice().isGreaterThan(creditEntry.getTotalCreditAmount())) {
            String paymentErrorMessage = "Customer with id %s doesn't have enough credit for payment".formatted(payment.getCustomerId().getValue());
            log.error(paymentErrorMessage);
            failureMessages.add(paymentErrorMessage);
        }
    }

    @Override
    public PaymentEvent validateAndCancelPayment(
            Payment payment,
            CreditEntry creditEntry,
            List<CreditHistory> creditHistory,
            List<String> failureMessages
    ) {
        payment.validatePayment(failureMessages);
        addCreditEntry(payment, creditEntry);
        updateCreditHistory(payment, creditHistory, TransactionType.CREDIT);

        if (failureMessages.isEmpty()) {
            log.info("Payment with id %s is cancelled for order id %s".formatted(payment.getId().getValue(), payment.getOrderId().getValue()));
            payment.updateStatus(PaymentStatus.CANCELLED);
            return new PaymentCancelledEvent(payment);
        }
        log.info("Payment initiation with id: %s failed for order id: %s".formatted(payment.getId().getValue(), payment.getOrderId().getValue()));
        payment.updateStatus(PaymentStatus.FAILED);
        return new PaymentFailedEvent(payment, failureMessages);
    }

    private void addCreditEntry(Payment payment, CreditEntry creditEntry) {
        creditEntry.addAmount(payment.getPrice());
    }
}
