package ru.practicum.mainService.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.mainService.category.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
