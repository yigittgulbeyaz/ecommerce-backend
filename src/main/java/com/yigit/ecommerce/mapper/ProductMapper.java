package com.yigit.ecommerce.mapper;

import com.yigit.ecommerce.dto.response.product.ProductResponse;
import com.yigit.ecommerce.model.category.Category;
import com.yigit.ecommerce.model.product.Product;

public final class ProductMapper {

    private ProductMapper() {}

    public static ProductResponse toResponse(Product p) {
        if (p == null) return null;

        ProductResponse r = new ProductResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setDescription(p.getDescription());
        r.setPrice(p.getPrice());

        Category c = p.getCategory();
        if (c != null) {
            r.setCategoryId(c.getId());
            r.setCategoryName(c.getName());
        }
        return r;
    }
}
