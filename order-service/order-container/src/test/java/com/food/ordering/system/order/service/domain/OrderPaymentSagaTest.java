package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.valueobject.PaymentStatus;
import com.food.ordering.system.order.service.dataaccess.outbox.payment.repository.PaymentOutboxJpaRepository;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.ordering.saga.SagaStatus;
import com.food.ordering.system.ordering.saga.order.SagaConstants;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = OrderServiceApplication.class)
@SqlGroup({
        @Sql(value = "classpath:sql/OrderPaymentSagaTestSetUp.sql"),
        @Sql(value = "classpath:sql/OrderPaymentSagaTestCleanUp.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class OrderPaymentSagaTest {
    private static final Logger log = LoggerFactory.getLogger(OrderPaymentSagaTest.class);

    @Autowired
    private OrderPaymentSaga orderPaymentSaga;

    @Autowired
    private PaymentOutboxJpaRepository paymentOutboxJpaRepository;

    private final UUID SAGA_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afa");
    private final UUID ORDER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb17");
    private final UUID CUSTOMER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");
    private final UUID PAYMENT_ID = UUID.randomUUID();
    private final BigDecimal PRICE = new BigDecimal("100");

    @Test
    void testDoublePayment() {
        orderPaymentSaga.process(getPaymentResponse());
        orderPaymentSaga.process(getPaymentResponse());
    }

    @Test
    void testConcurrentDoublePaymentWithThreads() throws InterruptedException {
        Thread thread1 = new Thread(() -> orderPaymentSaga.process(getPaymentResponse()));
        Thread thread2 = new Thread(() -> orderPaymentSaga.process(getPaymentResponse()));

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        assertPaymentOutbox();
    }

    @Test
    void testConcurrentDoublePaymentWithCountDownLatch() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(2);

        Thread thread1 = new Thread(() -> {
            try {
                orderPaymentSaga.process(getPaymentResponse());
            } catch (OptimisticLockingFailureException e) {
                log.error("Optimistic lock thrown in thread 1");
            } finally {
                latch.countDown();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                orderPaymentSaga.process(getPaymentResponse());
            } catch (OptimisticLockingFailureException e) {
                log.error("Optimistic lock thrown in thread 2");
            } finally {
                latch.countDown();
            }
        });

        thread1.start();
        thread2.start();

        latch.await();
    }

    private void assertPaymentOutbox() {
        assertThat(paymentOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(
                    SagaConstants.ORDER_SAGA_NAME,
                    SAGA_ID,
                    SagaStatus.PROCESSING
                )
        ).isPresent();
    }

    private PaymentResponse getPaymentResponse() {
        return PaymentResponse.builder()
                .id(UUID.randomUUID().toString())
                .paymentId(PAYMENT_ID.toString())
                .orderId(ORDER_ID.toString())
                .customerId(CUSTOMER_ID.toString())
                .sagaId(SAGA_ID.toString())
                .price(PRICE)
                .paymentStatus(PaymentStatus.COMPLETED)
                .failureMessages(new ArrayList<>())
                .createdAt(Instant.now())
                .build();
    }
}

