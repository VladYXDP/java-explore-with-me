package ru.practicum.ewm.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.categories.entity.Category;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    @Override
    public Category addCategory(Category newCategoryDto) {
        return null;
    }

    @Override
    public Category updateCategory(Long categoryId, Category categoryDto) {
        return null;
    }

    @Override
    public List<Category> getCategories(Integer from, Integer size) {
        return null;
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return null;
    }

    @Override
    public void deleteCategory(Long categoryId) {

    }
}
