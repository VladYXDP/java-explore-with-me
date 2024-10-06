package ru.practicum.ewm.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.categories.entity.Category;
import ru.practicum.ewm.categories.repository.CategoriesRepository;
import ru.practicum.ewm.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoriesRepository categoriesRepository;

    @Override
    public Category addCategory(Category category) {
        return categoriesRepository.save(category);
    }

    @Override
    public Category updateCategory(Category category) {
        Category currentCategory = getCategory(category.getId());
        currentCategory.setName(category.getName());
        return categoriesRepository.save(currentCategory);
    }

    @Override
    public List<Category> getCategories(Integer from, Integer size) {
        return categoriesRepository.findAll(PageRequest.of(from / size, size)).toList();
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return getCategory(categoryId);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        if (!categoriesRepository.existsById(categoryId)) {
            throw new NotFoundException("Category with id=" + categoryId + " was not found");
        }
        categoriesRepository.deleteById(categoryId);
    }

    private Category getCategory(Long categoryId) {
        return categoriesRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException("Category with id=" + categoryId + " was not found"));
    }
}
