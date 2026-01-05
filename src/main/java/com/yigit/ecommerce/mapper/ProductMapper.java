package com.yigit.ecommerce.mapper;

import com.yigit.ecommerce.dto.response.product.ProductResponse;
import com.yigit.ecommerce.model.category.Category;
import com.yigit.ecommerce.model.product.Product;

public final class ProductMapper {

    private ProductMapper() {
    }

    public static ProductResponse toResponse(Product p) {
        if (p == null)
            return null;

        Category c = p.getCategory();
        Long categoryId = null;
        String categoryName = null;

        if (c != null) {
            categoryId = c.getId();
            categoryName = c.getName();
        }

        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                categoryId,
                categoryName);
    }
}
