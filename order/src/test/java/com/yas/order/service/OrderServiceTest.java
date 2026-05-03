package com.yas.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.mapper.OrderMapper;
import com.yas.order.model.Order;
import com.yas.order.model.OrderItem;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.model.request.OrderRequest;
import com.yas.order.viewmodel.order.OrderBriefVm;
import com.yas.order.viewmodel.order.OrderGetVm;
import com.yas.order.viewmodel.order.OrderListVm;
import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import com.yas.order.viewmodel.order.OrderItemPostVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import com.yas.commonlibrary.utils.AuthenticationUtils;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductService productService;

    @Mock
    private CartService cartService;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private PromotionService promotionService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void getOrderWithItemsById_WhenNotFound_ShouldThrowException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.getOrderWithItemsById(999L));
    }

    @Test
    void getOrderWithItemsById_WhenFound_ShouldReturnVm() {
        OrderAddress address = mock(OrderAddress.class);
        Order order = Order.builder()
                .id(1L)
                .email("test@example.com")
                .orderStatus(OrderStatus.PENDING)
                .shippingAddressId(address)
                .billingAddressId(address)
                .build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderItem item = OrderItem.builder()
                .productId(1L)
                .orderId(1L)
                .quantity(1)
                .build();
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of(item));

        OrderVm orderVm = orderService.getOrderWithItemsById(1L);
        assertNotNull(orderVm);
        assertEquals(1L, orderVm.id());
        assertEquals(1, orderVm.orderItemVms().size());
    }

    @Test
    void updateOrderPaymentStatus_WhenCompleted_ShouldSetOrderStatusToPaid() {
        Order order = Order.builder()
                .id(1L)
                .email("test@example.com")
                .orderStatus(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentOrderStatusVm request = new PaymentOrderStatusVm(1L, "PAID", 123L, "COMPLETED");
        PaymentOrderStatusVm response = orderService.updateOrderPaymentStatus(request);

        assertEquals("PAID", response.orderStatus());
        assertEquals(OrderStatus.PAID, order.getOrderStatus());
        assertEquals(PaymentStatus.COMPLETED, order.getPaymentStatus());
    }

    @Test
    void createOrder_WhenValidRequest_ShouldSuccess() {
        OrderAddressPostVm address = new OrderAddressPostVm("John", "123", "Line1", "Line2", "City", "12345", 1L, "Dist", 1L, "State", 1L, "USA");
        OrderItemPostVm item = new OrderItemPostVm(1L, "Product", 2, new BigDecimal("100"), "Note", new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"));
        OrderPostVm request = new OrderPostVm("checkout1", "test@example.com", address, address, "Note", 0f, 0f, 1, new BigDecimal("200"), new BigDecimal("0"), "COUPON", null, null, PaymentStatus.PENDING, List.of(item));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(1L);
            return o;
        });
        when(orderItemRepository.saveAll(any())).thenAnswer(invocation -> {
            Iterable<OrderItem> items = invocation.getArgument(0);
            List<OrderItem> result = new java.util.ArrayList<>();
            items.forEach(result::add);
            return result;
        });
        when(orderRepository.findById(1L)).thenReturn(Optional.of(Order.builder().id(1L).build()));

        OrderVm orderVm = orderService.createOrder(request);

        assertNotNull(orderVm);
        assertEquals("test@example.com", orderVm.email());
        verify(productService).subtractProductStockQuantity(any());
        verify(cartService).deleteCartItems(any());
        verify(promotionService).updateUsagePromotion(anyList());
    }

    @Test
    void getLatestOrders_WhenCountValid_ShouldReturnList() {
        OrderAddress address = mock(OrderAddress.class);
        Order order = Order.builder()
                .id(1L)
                .billingAddressId(address)
                .build();
        when(orderRepository.getLatestOrders(any())).thenReturn(List.of(order));

        List<OrderBriefVm> result = orderService.getLatestOrders(1);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
    }

    @Test
    void getLatestOrders_WhenCountInvalid_ShouldReturnEmpty() {
        assertEquals(0, orderService.getLatestOrders(0).size());
        assertEquals(0, orderService.getLatestOrders(-1).size());
    }

    @Test
    void findOrderVmByCheckoutId_WhenFound_ShouldReturnVm() {
        OrderAddress address = mock(OrderAddress.class);
        Order order = Order.builder()
                .id(1L)
                .checkoutId("checkout123")
                .shippingAddressId(address)
                .billingAddressId(address)
                .build();
        when(orderRepository.findByCheckoutId("checkout123")).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of());

        OrderGetVm result = orderService.findOrderVmByCheckoutId("checkout123");
        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void findOrderByCheckoutId_WhenNotFound_ShouldThrowException() {
        when(orderRepository.findByCheckoutId("none")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.findOrderByCheckoutId("none"));
    }

    @Test
    void getAllOrder_ShouldReturnList() {
        OrderAddress address = mock(OrderAddress.class);
        Order order = Order.builder().id(1L).billingAddressId(address).build();
        Page<Order> page = new PageImpl<>(List.of(order));
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        OrderListVm result = orderService.getAllOrder(
                Pair.of(ZonedDateTime.now(), ZonedDateTime.now()),
                null,
                List.of(),
                Pair.of("", ""),
                null,
                Pair.of(0, 10)
        );
        assertNotNull(result);
        assertEquals(1, result.orderList().size());
    }

    @Test
    void exportCsv_ShouldReturnBytes() throws IOException {
        OrderAddress address = mock(OrderAddress.class);
        when(address.getContactName()).thenReturn("John");
        when(address.getPhone()).thenReturn("123");
        when(address.getAddressLine1()).thenReturn("Line1");
        Order order = Order.builder()
                .id(1L)
                .email("test@example.com")
                .totalPrice(new BigDecimal("100"))
                .orderStatus(OrderStatus.PENDING)
                .billingAddressId(address)
                .shippingAddressId(address)
                .build();
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(order)));
        when(orderMapper.toCsv(any())).thenReturn(mock(com.yas.order.model.csv.OrderItemCsv.class));

        OrderRequest request = OrderRequest.builder()
                .createdFrom(ZonedDateTime.now())
                .createdTo(ZonedDateTime.now())
                .billingCountry("USA")
                .billingPhoneNumber("123")
                .orderStatus(List.of())
                .pageNo(0)
                .pageSize(10)
                .build();
        byte[] result = orderService.exportCsv(request);
        assertNotNull(result);
    }

    @Test
    void getMyOrders_ShouldReturnList() {
        try (MockedStatic<AuthenticationUtils> mocked = Mockito.mockStatic(AuthenticationUtils.class)) {
            mocked.when(AuthenticationUtils::extractUserId).thenReturn("user1");
            Order order = Order.builder().id(1L).build();
            when(orderRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(order));

            List<OrderGetVm> result = orderService.getMyOrders("product", OrderStatus.PENDING);
            assertEquals(1, result.size());
        }
    }

    @Test
    void rejectOrder_ShouldUpdateStatus() {
        Order order = Order.builder()
                .id(1L)
                .email("test@example.com")
                .orderStatus(OrderStatus.PENDING)
                .build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.rejectOrder(1L, "Bad customer");

        verify(orderRepository).save(order);
        assertEquals(OrderStatus.REJECT, order.getOrderStatus());
        assertEquals("Bad customer", order.getRejectReason());
    }

    @Test
    void acceptOrder_ShouldUpdateStatus() {
        Order order = Order.builder()
                .id(1L)
                .email("test@example.com")
                .orderStatus(OrderStatus.PENDING)
                .build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.acceptOrder(1L);

        verify(orderRepository).save(order);
        assertEquals(OrderStatus.ACCEPTED, order.getOrderStatus());
    }
}
