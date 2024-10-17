package ru.practicum.ewm.categories.service;

import ru.practicum.ewm.categories.entity.Category;

import java.util.List;

public interface CategoryService {

    Category addCategory(Category newCategoryDto);

    Category updateCategory(Category categoryDto);

    List<Category> getCategories(Integer from, Integer size);

    Category getCategoryById(Long categoryId);

    void deleteCategory(Long categoryId);
}
