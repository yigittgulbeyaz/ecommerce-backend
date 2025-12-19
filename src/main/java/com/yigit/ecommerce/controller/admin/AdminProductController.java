package com.yigit.ecommerce.controller.admin;

import com.yigit.ecommerce.common.ApiResponse;
import com.yigit.ecommerce.dto.request.product.ProductCreateRequest;
import com.yigit.ecommerce.dto.request.product.ProductUpdateRequest;
import com.yigit.ecommerce.dto.response.product.ProductResponse;
import com.yigit.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ApiResponse<ProductResponse> create(@Valid @RequestBody ProductCreateRequest request) {
        return ApiResponse.created(productService.create(request), "Product created");
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request
    ) {
        return ApiResponse.success(productService.update(id, request), "Product updated");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.success("Product deleted");
    }
}
