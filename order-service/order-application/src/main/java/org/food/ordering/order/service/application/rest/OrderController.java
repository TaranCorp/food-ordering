package org.food.ordering.order.service.application.rest;

import lombok.extern.slf4j.Slf4j;
import org.food.ordering.order.service.domain.dto.create.CreateOrderCommand;
import org.food.ordering.order.service.domain.dto.create.CreateOrderResponse;
import org.food.ordering.order.service.domain.dto.track.TrackOrderResponse;
import org.food.ordering.order.service.domain.port.input.service.OrderApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.food.ordering.order.service.domain.dto.track.TrackOrderQuery.createTrackerBy;

@Slf4j
@RestController
@RequestMapping(value = "/orders", produces = "application/vnd.api.v1+json")
class OrderController {

    private final OrderApplicationService orderApplicationService;

    OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderCommand orderCommand) {
        log.info(
                "Creating order for customer: {} at restaurant: {}",
                orderCommand.getCustomerId(), orderCommand.getRestaurantId()
        );

        CreateOrderResponse orderResponse = orderApplicationService.createOrder(orderCommand);

        log.info(
                "Tracking id: {} of order with status: {}",
                orderResponse.getOrderTrackingId(), orderResponse.getOrderStatus()
        );

        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }

    @GetMapping("{trackingId}")
    public ResponseEntity<TrackOrderResponse> getOrderByTrackingId(@PathVariable UUID trackingId) {
        return ResponseEntity.ok(orderApplicationService.trackOrder(createTrackerBy(trackingId)));
    }
}
