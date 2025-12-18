package com.yigit.ecommerce.service;

import com.yigit.ecommerce.dto.request.product.ProductCreateRequest;
import com.yigit.ecommerce.dto.request.product.ProductUpdateRequest;
import com.yigit.ecommerce.dto.response.product.ProductResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponse create(ProductCreateRequest request);

    ProductResponse getById(Long id);

    Page<ProductResponse> getAll(Long categoryId, String search, Pageable pageable);

    ProductResponse update(Long id, ProductUpdateRequest request);

    void delete(Long id);
}
