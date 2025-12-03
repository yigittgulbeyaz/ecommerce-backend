package com.yigit.ecommerce.repository;

import com.yigit.ecommerce.model.product.Product;
import com.yigit.ecommerce.model.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(Category category);

    List<Product> findByNameContainingIgnoreCase(String name);
}
