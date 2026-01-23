package com.yigit.ecommerce.service.impl;

import com.yigit.ecommerce.dto.request.order.CheckoutRequest;
import com.yigit.ecommerce.dto.response.order.OrderResponse;
import com.yigit.ecommerce.exception.ConflictException;
import com.yigit.ecommerce.exception.NotFoundException;
import com.yigit.ecommerce.exception.PaymentFailedException;
import com.yigit.ecommerce.mapper.OrderMapper;
import com.yigit.ecommerce.model.address.Address;
import com.yigit.ecommerce.model.cart.Cart;
import com.yigit.ecommerce.model.cart.CartItem;
import com.yigit.ecommerce.model.order.Order;
import com.yigit.ecommerce.model.order.OrderItem;
import com.yigit.ecommerce.model.order.OrderStatus;
import com.yigit.ecommerce.model.payment.Payment;
import com.yigit.ecommerce.model.product.Product;
import com.yigit.ecommerce.repository.AddressRepository;
import com.yigit.ecommerce.repository.CartRepository;
import com.yigit.ecommerce.repository.OrderRepository;
import com.yigit.ecommerce.security.context.AuthenticationContext;
import com.yigit.ecommerce.service.OrderService;
import com.yigit.ecommerce.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final PaymentService paymentService;
    private final AuthenticationContext authenticationContext;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CartRepository cartRepository,
                            AddressRepository addressRepository,
                            PaymentService paymentService,
                            AuthenticationContext authenticationContext) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.paymentService = paymentService;
        this.authenticationContext = authenticationContext;
    }

    @Override
    public OrderResponse checkout(CheckoutRequest request) {

        // ===============================
        // 1. AUTH CONTEXT & BASIC FETCH
        // ===============================
        Long userId = authenticationContext.requireUserId();

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found"));

        // ===============================
        // 2. VALIDATION
        // (Candidate for OrderValidator)
        // ===============================

        // Cart must not be empty
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new ConflictException("Cart is empty");
        }

        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new NotFoundException("Address not found"));

        // Validate address ownership (security concern)
        if (!address.isOwnedBy(userId)) {
            throw new NotFoundException("Address not found for user");
        }

        // ===============================
        // 3. ORDER CREATION & SNAPSHOT
        // (Candidate for OrderFactory)
        // ===============================

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(OrderStatus.PENDING);

        // Address snapshot
        order.setShippingAddressTitle(address.getTitle());
        order.setShippingAddressLine(address.getAddressLine());
        order.setShippingAddressCity(address.getCity());
        order.setShippingAddressDistrict(address.getDistrict());
        order.setShippingAddressZipCode(address.getZipCode());
        order.setShippingAddressCountry(address.getCountry());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem ci : cart.getItems()) {
            Product p = ci.getProduct();
            int qty = ci.getQuantity();

            // Validate item data
            if (qty <= 0) {
                throw new ConflictException("Invalid quantity");
            }
            if (p.getPrice() == null) {
                throw new ConflictException("Product price missing");
            }

            BigDecimal unitPrice = p.getPrice();
            total = total.add(unitPrice.multiply(BigDecimal.valueOf(qty)));

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(p);
            oi.setQuantity(qty);
            oi.setPrice(unitPrice); // price snapshot

            orderItems.add(oi);
        }

        order.setItems(orderItems);
        order.setTotalPrice(total);

        // Persist order in PENDING state
        Order savedOrder = orderRepository.save(order);

        // ===============================
        // 4. PAYMENT PROCESS (Side-effect)
        // ===============================

        Payment payment = paymentService.pay(
                savedOrder.getId(),
                request.paymentRequest()
        );

        if (!payment.isSuccessful()) {
            // Transaction will be rolled back
            throw new PaymentFailedException("Payment failed. Transaction rolled back.");
        }

        // ===============================
        // 5. STATE TRANSITION & CLEANUP
        // ===============================

        savedOrder.setStatus(OrderStatus.PAID);

        // Clear cart only after successful payment
        cart.getItems().clear();
        cartRepository.save(cart);

        return OrderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders() {
        Long userId = authenticationContext.requireUserId();
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getMyOrder(Long id) {
        Long userId = authenticationContext.requireUserId();
        Order order = orderRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        return OrderMapper.toResponse(order);
    }
}
