package com.yigit.ecommerce.repository;

import com.yigit.ecommerce.model.order.OrderItem;
import com.yigit.ecommerce.model.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);
}
