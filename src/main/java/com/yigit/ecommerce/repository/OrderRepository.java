package com.yigit.ecommerce.repository;

import com.yigit.ecommerce.model.order.Order;
import com.yigit.ecommerce.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);
}
