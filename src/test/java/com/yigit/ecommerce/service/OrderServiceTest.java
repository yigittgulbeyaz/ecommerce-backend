package com.yigit.ecommerce.service;

import com.yigit.ecommerce.dto.request.order.CheckoutRequest;
import com.yigit.ecommerce.dto.request.payment.PaymentRequest;
import com.yigit.ecommerce.exception.ConflictException;
import com.yigit.ecommerce.exception.PaymentFailedException;
import com.yigit.ecommerce.model.address.Address;
import com.yigit.ecommerce.model.cart.Cart;
import com.yigit.ecommerce.model.cart.CartItem;
import com.yigit.ecommerce.model.order.Order;
import com.yigit.ecommerce.model.order.OrderStatus;
import com.yigit.ecommerce.model.payment.Payment;
import com.yigit.ecommerce.model.product.Product;
import com.yigit.ecommerce.model.user.User;
import com.yigit.ecommerce.repository.AddressRepository;
import com.yigit.ecommerce.repository.CartRepository;
import com.yigit.ecommerce.repository.OrderRepository;
import com.yigit.ecommerce.security.context.AuthenticationContext;
import com.yigit.ecommerce.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    CartRepository cartRepository;

    @Mock
    AddressRepository addressRepository;

    @Mock
    PaymentService paymentService;

    @Mock
    AuthenticationContext authenticationContext;

    @InjectMocks
    OrderServiceImpl orderService;

    @Test
    void checkout_success_shouldCreatePaidOrder_andClearCart() {
        Long userId = 1L;
        when(authenticationContext.requireUserId()).thenReturn(userId);

        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(100));

        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(2);

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        cart.getItems().add(item);

        when(cartRepository.findByUserId(userId))
                .thenReturn(Optional.of(cart));

        Address address = mock(Address.class);
        when(address.isOwnedBy(userId)).thenReturn(true);

        when(addressRepository.findById(10L))
                .thenReturn(Optional.of(address));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(inv -> {
                    Order o = inv.getArgument(0);
                    o.setId(99L);
                    return o;
                });

        Payment payment = mock(Payment.class);
        when(payment.isSuccessful()).thenReturn(true);

        when(paymentService.pay(anyLong(), any()))
                .thenReturn(payment);

        CheckoutRequest request =
                new CheckoutRequest(10L, mock(PaymentRequest.class));

        var response = orderService.checkout(request);

        assertThat(response.status()).isEqualTo(OrderStatus.PAID);
        assertThat(cart.getItems()).isEmpty();

        verify(cartRepository).save(cart);
    }

    @Test
    void checkout_shouldFail_whenCartIsEmpty() {
        Long userId = 1L;
        when(authenticationContext.requireUserId()).thenReturn(userId);

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());

        when(cartRepository.findByUserId(userId))
                .thenReturn(Optional.of(cart));

        CheckoutRequest request =
                new CheckoutRequest(1L, mock(PaymentRequest.class));

        assertThatThrownBy(() -> orderService.checkout(request))
                .isInstanceOf(ConflictException.class);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void checkout_shouldThrowPaymentFailedException_whenPaymentFails() {
        Long userId = 1L;
        when(authenticationContext.requireUserId()).thenReturn(userId);

        Product product = new Product();
        product.setPrice(BigDecimal.TEN);

        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(1);

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        cart.getItems().add(item);

        when(cartRepository.findByUserId(userId))
                .thenReturn(Optional.of(cart));

        Address address = mock(Address.class);
        when(address.isOwnedBy(userId)).thenReturn(true);

        when(addressRepository.findById(1L))
                .thenReturn(Optional.of(address));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(inv -> {
                    Order o = inv.getArgument(0);
                    o.setId(5L);
                    return o;
                });

        Payment payment = mock(Payment.class);
        when(payment.isSuccessful()).thenReturn(false);

        when(paymentService.pay(anyLong(), any()))
                .thenReturn(payment);

        CheckoutRequest request =
                new CheckoutRequest(1L, mock(PaymentRequest.class));

        assertThatThrownBy(() -> orderService.checkout(request))
                .isInstanceOf(PaymentFailedException.class);

        assertThat(cart.getItems()).hasSize(1);
    }
}
