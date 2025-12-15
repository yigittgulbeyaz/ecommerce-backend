package com.yigit.ecommerce.mapper;

import com.yigit.ecommerce.dto.response.category.CategoryResponse;
import com.yigit.ecommerce.model.category.Category;

import java.util.List;

public final class CategoryMapper {

    private CategoryMapper() {}

    public static CategoryResponse toResponse(Category category) {
        if (category == null) return null;
        return new CategoryResponse(category.getId(), category.getName());
    }

    public static List<CategoryResponse> toResponseList(List<Category> categories) {
        if (categories == null) return List.of();
        return categories.stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }
}
