package ru.practicum.mainService.category.mapper;

import ru.practicum.mainService.category.dto.CategoryDto;
import ru.practicum.mainService.category.dto.NewCategoryDto;
import ru.practicum.mainService.category.model.Category;

public class CategoryMapper {
    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toCategory(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }
}