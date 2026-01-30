package com.yigit.ecommerce.service.admin;

import com.yigit.ecommerce.dto.response.admin.AdminStatsResponse;
import com.yigit.ecommerce.model.order.OrderStatus;
import com.yigit.ecommerce.repository.CategoryRepository;
import com.yigit.ecommerce.repository.OrderRepository;
import com.yigit.ecommerce.repository.ProductRepository;
import com.yigit.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AdminStatsService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public AdminStatsService(
            ProductRepository productRepository,
            OrderRepository orderRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public AdminStatsResponse getStats() {
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();
        long totalCategories = categoryRepository.count();
        long totalUsers = userRepository.count();

        // Calculate revenue from paid/shipped orders (not cancelled or pending)
        BigDecimal revenue = orderRepository.findAll().stream()
                .filter(order -> order.getStatus() == OrderStatus.PAID ||
                        order.getStatus() == OrderStatus.SHIPPED)
                .map(order -> order.getTotalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AdminStatsResponse(
                totalProducts,
                totalOrders,
                totalCategories,
                totalUsers,
                revenue);
    }
}
