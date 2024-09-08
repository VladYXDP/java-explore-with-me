package ru.practicum.ewm.categories.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.categories.entity.Category;

public interface CategoriesRepository extends JpaRepository<Category, Long> {

    
}
