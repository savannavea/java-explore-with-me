package ru.practicum.mainService.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.category.dto.CategoryDto;
import ru.practicum.mainService.category.dto.NewCategoryDto;
import ru.practicum.mainService.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CategoryDto createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        return categoryService.createCategory(newCategoryDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{catId}")
    public void deleteCategoryById(@PathVariable("catId") @Positive Long catId) {
        categoryService.deleteCategoryById(catId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{catId}")
    public CategoryDto updateCategoryById(@PathVariable("catId") Long categoryId,
                                          @RequestBody @Valid NewCategoryDto newCategoryDto) {
        return categoryService.updateCategoryById(categoryId, newCategoryDto);
    }
}