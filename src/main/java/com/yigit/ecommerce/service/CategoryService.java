package com.yigit.ecommerce.service;

import com.yigit.ecommerce.dto.request.category.CategoryCreateRequest;
import com.yigit.ecommerce.dto.request.category.CategoryUpdateRequest;
import com.yigit.ecommerce.dto.response.category.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse create(CategoryCreateRequest request);

    CategoryResponse update(Long id, CategoryUpdateRequest request);

    CategoryResponse getById(Long id);

    List<CategoryResponse> getAll();

    void delete(Long id);
}
