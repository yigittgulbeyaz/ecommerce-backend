package com.yigit.ecommerce.controller.product;

import com.yigit.ecommerce.common.ApiResponse;
import com.yigit.ecommerce.dto.common.PageMapper;
import com.yigit.ecommerce.dto.common.PageResponse;
import com.yigit.ecommerce.dto.response.product.ProductResponse;
import com.yigit.ecommerce.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(productService.getById(id), "Product fetched");
    }

    @GetMapping
    public ApiResponse<PageResponse<ProductResponse>> getAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {
        Page<ProductResponse> page = productService.getAll(categoryId, search, pageable);
        return ApiResponse.success(PageMapper.toResponse(page), "Products listed");
    }
}
