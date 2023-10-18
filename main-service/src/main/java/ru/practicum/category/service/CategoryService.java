package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategoryById(Long catId);

    CategoryDto updateCategoryById(Long categoryId, NewCategoryDto newCategoryDto);

    List<CategoryDto> getCategory(Integer from, Integer size);

    CategoryDto getCategoryId(Long catId);

    Category getCategoryOrElseThrow(Long id);
}
