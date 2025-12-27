package com.yigit.ecommerce.service.impl;

import com.yigit.ecommerce.dto.response.order.OrderResponse;
import com.yigit.ecommerce.exception.ConflictException;
import com.yigit.ecommerce.exception.NotFoundException;
import com.yigit.ecommerce.mapper.OrderMapper;
import com.yigit.ecommerce.model.cart.Cart;
import com.yigit.ecommerce.model.cart.CartItem;
import com.yigit.ecommerce.model.order.Order;
import com.yigit.ecommerce.model.order.OrderItem;
import com.yigit.ecommerce.model.order.OrderStatus;
import com.yigit.ecommerce.model.product.Product;
import com.yigit.ecommerce.repository.CartRepository;
import com.yigit.ecommerce.repository.OrderRepository;
import com.yigit.ecommerce.security.context.AuthenticationContext;
import com.yigit.ecommerce.service.OrderService;
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
    private final AuthenticationContext authenticationContext;

    public OrderServiceImpl(OrderRepository orderRepository,
                                CartRepository cartRepository,
                                AuthenticationContext authenticationContext) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.authenticationContext = authenticationContext;
    }

    @Override
    public OrderResponse checkout() {
        Long userId = authenticationContext.requireUserId();

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found for user: " + userId));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new ConflictException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem ci : cart.getItems()) {
            Product p = ci.getProduct();
            int qty = ci.getQuantity();

            if (qty <= 0) throw new ConflictException("Invalid cart item quantity");

            BigDecimal unitPrice = p.getPrice();
            if (unitPrice == null) throw new ConflictException("Product price missing: " + p.getId());

            total = total.add(unitPrice.multiply(BigDecimal.valueOf(qty)));

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(p);
            oi.setQuantity(qty);
            oi.setPrice(unitPrice); // snapshot

            orderItems.add(oi);
        }

        order.setItems(orderItems);
        order.setTotalPrice(total);

        Order saved = orderRepository.save(order);

        // cart temizle: orphanRemoval=true => cart_items delete
        cart.getItems().clear();
        cartRepository.save(cart);

        return OrderMapper.toResponse(saved);
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
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));

        return OrderMapper.toResponse(order);
    }
}
