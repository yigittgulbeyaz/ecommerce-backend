package com.yigit.ecommerce.service.impl;

import com.yigit.ecommerce.dto.request.category.CategoryCreateRequest;
import com.yigit.ecommerce.dto.request.category.CategoryUpdateRequest;
import com.yigit.ecommerce.dto.response.category.CategoryResponse;
import com.yigit.ecommerce.exception.ConflictException;
import com.yigit.ecommerce.exception.NotFoundException;
import com.yigit.ecommerce.mapper.CategoryMapper;
import com.yigit.ecommerce.model.category.Category;
import com.yigit.ecommerce.repository.CategoryRepository;
import com.yigit.ecommerce.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryResponse create(CategoryCreateRequest request) {
        String name = request.name().trim();

        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("Category name already exists");
        }

        Category category = new Category();
        category.setName(name);

        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse update(Long id, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        String newName = request.name().trim();

        if (!category.getName().equalsIgnoreCase(newName) && categoryRepository.existsByNameIgnoreCase(newName)) {
            throw new ConflictException("Category name already exists");
        }

        category.setName(newName);
        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        return CategoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        categoryRepository.delete(category);
    }
}
