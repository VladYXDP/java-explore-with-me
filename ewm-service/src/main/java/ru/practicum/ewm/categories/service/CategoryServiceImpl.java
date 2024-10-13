package ru.practicum.ewm.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.categories.entity.Category;
import ru.practicum.ewm.categories.repository.CategoriesRepository;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exceptions.AlreadyExistException;
import ru.practicum.ewm.exceptions.ForbiddenException;
import ru.practicum.ewm.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final EventRepository eventRepository;
    private final CategoriesRepository categoriesRepository;

    @Override
    public Category addCategory(Category category) {
        checkCategoryNameForCreate(category.getName());
        return categoriesRepository.save(category);
    }

    @Override
    public Category updateCategory(Category category) {
        checkCategoryNameForUpdate(category.getName(), category.getId());
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
        Optional<Category> category = categoriesRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new NotFoundException("Category with id=" + categoryId + " was not found");
        }
        if (eventRepository.findFirstByCategory(category.get()).isPresent()) {
            throw new ForbiddenException("Ошибка удаления категории! Существуют события этой категории!");
        }
        categoriesRepository.deleteById(categoryId);
    }

    private Category getCategory(Long categoryId) {
        return categoriesRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException("Category with id=" + categoryId + " was not found"));
    }

    private void checkCategoryNameForUpdate(String name, Long categoryId) {
        if (categoriesRepository.existsCategoryByNameAndIdIsNot(name, categoryId)) {
            throw new AlreadyExistException("Ошибка обновления категории! Название (" + name + ") категории!");
        }
    }

    private void checkCategoryNameForCreate(String name) {
        if (categoriesRepository.existsCategoryByName(name)) {
            throw new AlreadyExistException("Ошибка добавления категории! Название (" + name + ") категории!");
        }
    }
}
