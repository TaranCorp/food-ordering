package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.domain.valueobject.PaymentStatus;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.outbox.dto.OrderApprovalOutboxPersistDto;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.system.order.service.domain.outbox.scheduler.approval.ApprovalOutboxHelper;
import com.food.ordering.system.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.ordering.outbox.OutboxStatus;
import com.food.ordering.system.ordering.saga.SagaStatus;
import com.food.ordering.system.ordering.saga.SagaStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.food.ordering.system.domain.DomainConstants.UTC;

@Slf4j
@Component
public class OrderPaymentSaga implements SagaStep<PaymentResponse> {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final OrderSagaHelper orderSagaHelper;
    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final OrderDataMapper orderDataMapper;

    public OrderPaymentSaga(OrderDomainService orderDomainService,
                            OrderRepository orderRepository,
                            PaymentOutboxHelper paymentOutboxHelper,
                            OrderSagaHelper orderSagaHelper,
                            ApprovalOutboxHelper approvalOutboxHelper,
                            OrderDataMapper orderDataMapper) {
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.paymentOutboxHelper = paymentOutboxHelper;
        this.orderSagaHelper = orderSagaHelper;
        this.approvalOutboxHelper = approvalOutboxHelper;
        this.orderDataMapper = orderDataMapper;
    }

    @Override
    @Transactional
    public void process(PaymentResponse paymentResponse) {
        final Optional<OrderPaymentOutboxMessage> paymentOutboxMessage = paymentOutboxHelper.getPaymentOutboxMessage(
                UUID.fromString(paymentResponse.getSagaId()),
                SagaStatus.STARTED
        );

        if (paymentOutboxMessage.isEmpty()) {
            log.info("An outbox message with saga id: {} is already processed", paymentResponse.getSagaId());
            return;
        }

        final OrderPaidEvent domainEvent = completePaymentForOrder(paymentResponse);

        final SagaStatus sagaStatus = orderSagaHelper.sagaStatusFromOrderStatus(domainEvent.getOrder().getOrderStatus());
        paymentOutboxHelper.save(
                getUpdatedPaymentOutboxMessage(paymentOutboxMessage.get(), domainEvent.getOrder().getOrderStatus(), sagaStatus)
        );

        approvalOutboxHelper.saveApprovalOutboxMessage(
                new OrderApprovalOutboxPersistDto(
                        orderDataMapper.orderApprovalEventPayloadFromOrderPaidEvent(domainEvent),
                        domainEvent.getOrder().getOrderStatus(),
                        OutboxStatus.STARTED,
                        sagaStatus,
                        UUID.fromString(paymentResponse.getSagaId())
                )
        );

        log.info("Order with id: {} is paid", domainEvent.getOrder().getId().getValue());
    }

    private OrderPaidEvent completePaymentForOrder(PaymentResponse paymentResponse) {
        log.info("Completing payment for order with id: {}", paymentResponse.getOrderId());
        final Order order = findOrder(paymentResponse.getOrderId());
        final OrderPaidEvent domainEvent = orderDomainService.payOrder(order);
        orderRepository.save(order);
        return domainEvent;
    }

    private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(OrderPaymentOutboxMessage orderPaymentOutboxMessage, OrderStatus orderStatus, SagaStatus sagaStatus) {
        orderPaymentOutboxMessage.setOrderStatus(orderStatus);
        orderPaymentOutboxMessage.setSagaStatus(sagaStatus);
        orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
        return orderPaymentOutboxMessage;
    }

    @Override
    @Transactional
    public void rollback(PaymentResponse paymentResponse) {
        paymentOutboxHelper.getPaymentOutboxMessage(
                UUID.fromString(paymentResponse.getSagaId()),
                getAccurateSagaStatus(paymentResponse.getPaymentStatus())
        ).ifPresentOrElse(
                outboxMessage -> {
                    final Order order = rollbackPaymentForOrder(paymentResponse);
                    paymentOutboxHelper.save(
                            getUpdatedPaymentOutboxMessage(
                                    outboxMessage,
                                    order.getOrderStatus(),
                                    orderSagaHelper.sagaStatusFromOrderStatus(order.getOrderStatus())
                            )
                    );

                    if (paymentResponse.getPaymentStatus() == PaymentStatus.CANCELLED) {
                        approvalOutboxHelper.save(
                                getUpdatedOrderApprovalOutboxMessage(
                                        paymentResponse.getSagaId(),
                                        order.getOrderStatus(),
                                        orderSagaHelper.sagaStatusFromOrderStatus(order.getOrderStatus()))
                        );
                    }

                    log.info("Order with id: {} is cancelled", order.getId().getValue());
                },
                () -> log.info("An outbox message with saga id: {} is already roll backed", paymentResponse.getSagaId())
        );
    }

    private OrderApprovalOutboxMessage getUpdatedOrderApprovalOutboxMessage(String sagaId, OrderStatus orderStatus, SagaStatus sagaStatus) {
        return approvalOutboxHelper.getApprovalOutboxMessage(
                UUID.fromString(sagaId),
                SagaStatus.COMPENSATING
        ).map(
            outboxMessage -> {
                outboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
                outboxMessage.setOrderStatus(orderStatus);
                outboxMessage.setSagaStatus(sagaStatus);
                return outboxMessage;
            }
        ).orElseThrow(this::throwNotFoundOutboxMessage);
    }

    private OrderDomainException throwNotFoundOutboxMessage() {
        return new OrderDomainException("Approval outbox message could not be found in %s status".formatted(SagaStatus.COMPENSATING.name()));
    }


    private Order rollbackPaymentForOrder(PaymentResponse paymentResponse) {
        log.info("Cancelling order with id: {}", paymentResponse.getOrderId());
        final Order order = findOrder(paymentResponse.getOrderId());
        orderDomainService.cancelOrder(order, paymentResponse.getFailureMessages());
        orderRepository.save(order);
        return order;
    }

    private SagaStatus[] getAccurateSagaStatus(PaymentStatus paymentStatus) {
        return switch (paymentStatus) {
            case COMPLETED -> new SagaStatus[] {SagaStatus.STARTED};
            case CANCELLED -> new SagaStatus[] {SagaStatus.PROCESSING};
            case FAILED -> new SagaStatus[] {SagaStatus.STARTED, SagaStatus.PROCESSING};
        };
    }

    private Order findOrder(String orderId) {
        Optional<Order> orderResponse = orderRepository.findById(new OrderId(UUID.fromString(orderId)));
        if (orderResponse.isEmpty()) {
            log.error("Order with id: {} could not be found!", orderId);
            throw new OrderNotFoundException("Order with id " + orderId + " could not be found!");
        }
        return orderResponse.get();
    }
}
