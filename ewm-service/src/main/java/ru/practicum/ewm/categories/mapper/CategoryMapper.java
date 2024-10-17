package ru.practicum.ewm.categories.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.CreateCategoryDto;
import ru.practicum.ewm.categories.entity.Category;

import java.util.List;

@Component
public class CategoryMapper {

    public Category toCategory(CreateCategoryDto newCategoryDto) {
        return new Category(newCategoryDto.getName());
    }

    public Category toCategory(CategoryDto categoryDto) {
        return new Category(
                categoryDto.getId(),
                categoryDto.getName());
    }

    public List<CategoryDto> toCategoryDto(List<Category> category) {
        return category.stream().map(this::toCategoryDto).toList();
    }

    public CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }
}