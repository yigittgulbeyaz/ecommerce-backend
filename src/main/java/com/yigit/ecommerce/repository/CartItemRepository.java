package com.yigit.ecommerce.repository;

import com.yigit.ecommerce.model.cart.CartItem;
import com.yigit.ecommerce.model.cart.Cart;
import com.yigit.ecommerce.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    void deleteAllByCart(Cart cart);
}
