package com.yigit.ecommerce.service.impl;

import com.yigit.ecommerce.dto.request.cart.AddCartItemRequest;
import com.yigit.ecommerce.dto.request.cart.UpdateCartItemQuantityRequest;
import com.yigit.ecommerce.dto.response.cart.CartItemResponse;
import com.yigit.ecommerce.dto.response.cart.CartResponse;
import com.yigit.ecommerce.exception.BadRequestException;
import com.yigit.ecommerce.exception.ConflictException;
import com.yigit.ecommerce.exception.NotFoundException;
import com.yigit.ecommerce.mapper.CartMapper;
import com.yigit.ecommerce.model.cart.Cart;
import com.yigit.ecommerce.model.cart.CartItem;
import com.yigit.ecommerce.model.product.Product;
import com.yigit.ecommerce.model.user.User;
import com.yigit.ecommerce.repository.CartItemRepository;
import com.yigit.ecommerce.repository.CartRepository;
import com.yigit.ecommerce.repository.ProductRepository;
import com.yigit.ecommerce.repository.UserRepository;
import com.yigit.ecommerce.security.context.AuthenticationContext;
import com.yigit.ecommerce.service.CartService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           AuthenticationContext authenticationContext) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
    }

    @Override
    public CartResponse getMyCart() {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);
        return buildCartResponse(cart);
    }

    @Override
    public CartResponse addItem(AddCartItemRequest request) {
        validateQuantity(request.getQuantity());

        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);
        Product product = getProductOrThrow(request.getProductId());

        try {
            CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                    .orElseGet(() -> {
                        CartItem ci = new CartItem();
                        ci.setCart(cart);
                        ci.setProduct(product);
                        ci.setQuantity(0);
                        return ci;
                    });

            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);

            if (cart.getItems() != null && !cart.getItems().contains(item)) {
                cart.getItems().add(item);
            }

            return buildCartResponse(cart);

        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Cart item conflict occurred. Please try again.");
        }
    }

    @Override
    public CartResponse updateQuantity(Long productId, UpdateCartItemQuantityRequest request) {
        validateQuantity(request.getQuantity());

        User user = getCurrentUser();
        Cart cart = getCartOrThrow(user);
        Product product = getProductOrThrow(productId);
        CartItem item = getCartItemOrThrow(cart, product, productId);

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return buildCartResponse(cart);
    }

    @Override
    public CartResponse removeItem(Long productId) {
        User user = getCurrentUser();
        Cart cart = getCartOrThrow(user);
        Product product = getProductOrThrow(productId);
        CartItem item = getCartItemOrThrow(cart, product, productId);

        cartItemRepository.delete(item);

        if (cart.getItems() != null) {
            cart.getItems().removeIf(ci ->
                    ci.getProduct() != null && ci.getProduct().getId().equals(productId)
            );
        }

        return buildCartResponse(cart);
    }

    @Override
    public CartResponse clear() {
        User user = getCurrentUser();
        Cart cart = getCartOrThrow(user);

        cartItemRepository.deleteAllByCart(cart);

        if (cart.getItems() != null) {
            cart.getItems().clear();
        }

        return buildCartResponse(cart);
    }

    // ===== helpers =====

    private CartResponse buildCartResponse(Cart cart) {
        CartResponse response = CartMapper.toResponse(cart);

        double total = 0.0;

        if (response.getItems() != null) {
            for (CartItemResponse item : response.getItems()) {
                double lineTotal = item.getUnitPrice() * item.getQuantity();
                item.setLineTotal(lineTotal);
                total += lineTotal;
            }
        }

        response.setTotal(total);
        return response;
    }

    private void validateQuantity(int quantity) {
        if (quantity < 1) {
            throw new BadRequestException("quantity must be at least 1");
        }
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setItems(new ArrayList<>());
            return cartRepository.save(cart);
        });
    }

    private Cart getCartOrThrow(User user) {
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Cart not found"));
    }

    private Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found: " + productId));
    }

    private CartItem getCartItemOrThrow(Cart cart, Product product, Long productIdForMessage) {
        return cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new NotFoundException("Cart item not found for product: " + productIdForMessage));
    }

    private User getCurrentUser() {
        Long userId = authenticationContext.requireUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }
}
