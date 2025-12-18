package com.yigit.ecommerce.service.impl;

import com.yigit.ecommerce.dto.request.product.ProductCreateRequest;
import com.yigit.ecommerce.dto.request.product.ProductUpdateRequest;
import com.yigit.ecommerce.dto.response.product.ProductResponse;
import com.yigit.ecommerce.exception.NotFoundException;
import com.yigit.ecommerce.mapper.ProductMapper;
import com.yigit.ecommerce.model.category.Category;
import com.yigit.ecommerce.model.product.Product;
import com.yigit.ecommerce.repository.CategoryRepository;
import com.yigit.ecommerce.repository.ProductRepository;
import com.yigit.ecommerce.service.ProductService;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public ProductResponse create(ProductCreateRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found: " + request.getCategoryId()));

        Product p = new Product();
        p.setName(request.getName());
        p.setDescription(request.getDescription());
        p.setPrice(request.getPrice());
        p.setCategory(category);

        return ProductMapper.toResponse(productRepository.save(p));
    }

    @Override
    public ProductResponse getById(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
        return ProductMapper.toResponse(p);
    }

    @Override
    public Page<ProductResponse> getAll(Long categoryId, String search, Pageable pageable) {
        boolean hasCategory = categoryId != null;
        boolean hasSearch = search != null && !search.isBlank();

        Page<Product> page;
        if (hasCategory && hasSearch) {
            page = productRepository.findByCategoryIdAndNameContainingIgnoreCase(categoryId, search.trim(), pageable);
        } else if (hasCategory) {
            page = productRepository.findByCategoryId(categoryId, pageable);
        } else if (hasSearch) {
            page = productRepository.findByNameContainingIgnoreCase(search.trim(), pageable);
        } else {
            page = productRepository.findAll(pageable);
        }

        return page.map(ProductMapper::toResponse);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found: " + request.getCategoryId()));

        p.setName(request.getName());
        p.setDescription(request.getDescription());
        p.setPrice(request.getPrice());
        p.setCategory(category);

        return ProductMapper.toResponse(productRepository.save(p));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new NotFoundException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }
}
