package com.yigit.ecommerce.integration;

import com.yigit.ecommerce.dto.request.auth.RegisterRequest;
import com.yigit.ecommerce.model.product.Product;
import com.yigit.ecommerce.model.category.Category;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class TestDataFactory {

    public RegisterRequest createRegisterRequest() {
        return new RegisterRequest(
                "Yigit",
                "yigit@test.com",
                "password123"
        );
    }

    public Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return category;
    }

    public Product createProduct(String name, BigDecimal price, Category category) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setCategory(category);
        product.setDescription("Test description for " + name);
        return product;
    }

    public Product createDefaultProduct(Category category) {
        return createProduct("Default Product", new BigDecimal("100.00"), category);
    }
}