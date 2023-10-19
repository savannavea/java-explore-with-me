package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(newCategoryDto)));
    }

    @Override
    public void deleteCategoryById(Long categoryId) {
        getCategoryOrElseThrow(categoryId);
        if (!eventRepository.findAllByCategoryId(categoryId).isEmpty()) {
            throw new ConflictException("The category is not empty");
        }

        categoryRepository.deleteById(categoryId);
    }

    @Override
    public CategoryDto updateCategoryById(Long categoryId, NewCategoryDto newCategoryDto) {
        Category category = getCategoryOrElseThrow(categoryId);
        newCategoryDto.setName(newCategoryDto.getName());

        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getCategory(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);

        return categoryRepository
                .findAll(pageable)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryId(Long categoryId) {
        Category category = getCategoryOrElseThrow(categoryId);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public Category getCategoryOrElseThrow(Long categoryId) {
        return categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Category with id=%s was not found", categoryId)));
    }
}
