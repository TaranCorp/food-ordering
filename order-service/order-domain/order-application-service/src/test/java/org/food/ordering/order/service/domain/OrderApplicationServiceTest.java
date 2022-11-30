package org.food.ordering.order.service.domain;

import org.food.ordering.domain.entity.Customer;
import org.food.ordering.domain.entity.Order;
import org.food.ordering.domain.entity.Product;
import org.food.ordering.domain.entity.Restaurant;
import org.food.ordering.domain.exception.OrderDomainException;
import org.food.ordering.domain.exception.OrderItemException;
import org.food.ordering.domain.valueobject.CustomerId;
import org.food.ordering.domain.valueobject.Money;
import org.food.ordering.domain.valueobject.OrderId;
import org.food.ordering.domain.valueobject.OrderStatus;
import org.food.ordering.domain.valueobject.ProductId;
import org.food.ordering.domain.valueobject.RestaurantId;
import org.food.ordering.order.service.domain.dto.create.CreateOrderCommand;
import org.food.ordering.order.service.domain.dto.create.CreateOrderResponse;
import org.food.ordering.order.service.domain.dto.create.OrderAddress;
import org.food.ordering.order.service.domain.dto.create.OrderItem;
import org.food.ordering.order.service.domain.mapper.OrderDataMapper;
import org.food.ordering.order.service.domain.port.input.service.OrderApplicationService;
import org.food.ordering.order.service.domain.port.output.repository.CustomerRepository;
import org.food.ordering.order.service.domain.port.output.repository.OrderRepository;
import org.food.ordering.order.service.domain.port.output.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {

    @Autowired
    private OrderApplicationService orderApplicationService;

    @Autowired
    private OrderDataMapper orderDataMapper;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private RestaurantRepository restaurantRepository;

    private CreateOrderCommand createOrderCommand;
    private CreateOrderCommand createOrderCommandWrongPrice;
    private CreateOrderCommand createOrderCommandWrongProductPrice;
    private final UUID CUSTOMER_ID = UUID.fromString("f001e8ae-6b30-11ed-a1eb-0242ac120002");
    private final UUID PRODUCT_ID = UUID.fromString("f001e8ae-6b30-11ed-a1eb-0242ac120003");
    private final UUID PRODUCT_ID_2 = UUID.fromString("f001e8ae-6b30-11ed-a1eb-0242ac120009");
    private final UUID RESTAURANT_ID = UUID.fromString("f001e8ae-6b30-11ed-a1eb-0242ac120004");
    private final UUID ORDER_ID = UUID.fromString("f001e8ae-6b30-11ed-a1eb-0242ac120005");
    private final BigDecimal PRICE = new BigDecimal("200.00");

    @BeforeEach
    public void init() {
        createOrderCommand = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("streetOne")
                        .city("cityOne")
                        .postalCode("22-400")
                        .build())
                .price(PRICE)
                .items(List.of(
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .price(new BigDecimal("50.00"))
                                .quantity(1)
                                .subTotal(new BigDecimal("50.00"))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID_2)
                                .price(new BigDecimal("50.00"))
                                .quantity(3)
                                .subTotal(new BigDecimal("150.00"))
                                .build()
                ))
                .build();

        createOrderCommandWrongPrice = CreateOrderCommand.builder()
                            .customerId(CUSTOMER_ID)
                            .restaurantId(RESTAURANT_ID)
                            .address(OrderAddress.builder()
                                    .street("streetOne")
                                    .city("cityOne")
                                    .postalCode("22-400")
                                    .build())
                            .price(new BigDecimal("250.00"))
                            .items(List.of(
                                    OrderItem.builder()
                                            .productId(PRODUCT_ID)
                                            .price(new BigDecimal("50.00"))
                                            .quantity(1)
                                            .subTotal(new BigDecimal("50.00"))
                                            .build(),
                                    OrderItem.builder()
                                            .productId(PRODUCT_ID_2)
                                            .price(new BigDecimal("50.00"))
                                            .quantity(3)
                                            .subTotal(new BigDecimal("150.00"))
                                            .build()
                            ))
                            .build();

        createOrderCommandWrongProductPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("streetOne")
                        .city("cityOne")
                        .postalCode("22-400")
                        .build())
                .price(new BigDecimal("210.00"))
                .items(List.of(
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .price(new BigDecimal("60.00"))
                                .quantity(1)
                                .subTotal(new BigDecimal("60.00"))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID_2)
                                .price(new BigDecimal("50.00"))
                                .quantity(3)
                                .subTotal(new BigDecimal("150.00"))
                                .build()
                ))
                .build();

        final Customer customer = new Customer();
        customer.setId(new CustomerId(CUSTOMER_ID));

        final Restaurant restaurantResponse = Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(List.of(
                            new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                            new Product(new ProductId(PRODUCT_ID_2), "product-2", new Money(new BigDecimal("50.00")))
                        )
                )
                .active(true)
                .build();

        Order order = orderDataMapper.createOrderFromOrderCommand(createOrderCommand);
        order.setId(new OrderId(ORDER_ID));

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findRestaurantInformation(
                orderDataMapper.createRestaurantFromOrderCommand(createOrderCommand))
        ).thenReturn(Optional.of(restaurantResponse));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
    }

    @Test
    void testCreateOrder() {
        CreateOrderResponse order = orderApplicationService.createOrder(createOrderCommand);
        assertEquals(order.getOrderStatus(), OrderStatus.PENDING);
        assertEquals(order.getMessage(), "Order created successfully");
        assertNotNull(order.getOrderTrackingId());
    }

    @Test
    void testCreateOrderWithWrongTotalPrice() {
        OrderDomainException orderDomainException = assertThrows(
                OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommandWrongPrice)
        );

        assertEquals(
                "Total price: 250.00 is not equal to orderItems total: 200.00",
                orderDomainException.getMessage()
        );
    }

    @Test
    void testCreateOrderWithWrongProductPrice() {
        OrderItemException orderItemException = assertThrows(
                OrderItemException.class,
                () -> orderApplicationService.createOrder(createOrderCommandWrongProductPrice)
        );

        assertEquals(
                "Cannot process incorrect order item price",
                orderItemException.getMessage()
        );
    }

    @Test
    void testCreateOrderWithInactiveRestaurant() {
        final Restaurant restaurantResponse = Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(List.of(
                                new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                                new Product(new ProductId(PRODUCT_ID_2), "product-2", new Money(new BigDecimal("50.00")))
                        )
                )
                .active(false)
                .build();

        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createRestaurantFromOrderCommand(createOrderCommand)))
                .thenReturn(Optional.of(restaurantResponse));

        OrderDomainException orderDomainException = assertThrows(
                OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommand)
        );

        assertEquals(
                "Restaurant with id " + RESTAURANT_ID + " is currently not active",
                orderDomainException.getMessage()
        );
    }
}
