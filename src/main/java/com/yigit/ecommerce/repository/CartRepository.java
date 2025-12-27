package com.yigit.ecommerce.repository;

import com.yigit.ecommerce.model.cart.Cart;
import com.yigit.ecommerce.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);

    Optional<Cart> findByUserId(Long userId);

    void deleteByUser(User user);

    User user(User user);
}
