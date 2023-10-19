package ru.practicum.mainService.category.service;

import ru.practicum.mainService.category.dto.CategoryDto;
import ru.practicum.mainService.category.dto.NewCategoryDto;
import ru.practicum.mainService.category.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategoryById(Long catId);

    CategoryDto updateCategoryById(Long categoryId, NewCategoryDto newCategoryDto);

    List<CategoryDto> getCategory(Integer from, Integer size);

    CategoryDto getCategoryId(Long catId);

    Category getCategoryOrElseThrow(Long id);
}
