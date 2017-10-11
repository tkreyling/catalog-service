package catalog.category;

import lombok.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@Value
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Category category = mapRequestToDomainObject(categoryRequest);

        Category savedCategory = categoryRepository.save(category);

        return mapDomainObjectToResponse(savedCategory);
    }

    private Category mapRequestToDomainObject(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setParentCategoryId(categoryRequest.getParentCategoryId());
        return category;
    }

    public Optional<CategoryResponseWithSubCategories> updateCategory(
            long categoryId, CategoryRequest categoryRequest) {
        List<Category> subCategories = categoryRepository.findByParentCategoryId(categoryId);
        return categoryRepository.findById(categoryId)
                .map(category -> updateAttributes(category, categoryRequest))
                .map(categoryRepository::save)
                .map(category -> mapDomainObjectToResponse(category, subCategories));
    }

    private Category updateAttributes(Category category, CategoryRequest categoryRequest) {
        category.setName(categoryRequest.getName());
        return category;
    }

    public Optional<CategoryResponseWithSubCategories> loadCategory(long categoryId) {
        List<Category> subCategories = categoryRepository.findByParentCategoryId(categoryId);
        return categoryRepository.findById(categoryId)
                .map(category -> mapDomainObjectToResponse(category, subCategories));
    }

    private CategoryResponseWithSubCategories mapDomainObjectToResponse(
            Category category, List<Category> subCategories) {
        return new CategoryResponseWithSubCategories(
                category.getId(),
                category.getName(),
                subCategories.stream()
                        .map(this::mapDomainObjectToResponse)
                        .collect(toList())
        );
    }

    private CategoryResponse mapDomainObjectToResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }

    public List<CategoryResponse> loadCategoryPath(Long categoryId) {
        List<CategoryResponse> categoryPath = new ArrayList<>();

        while (true) {
            categoryId = categoryRepository.findById(categoryId)
                    .map(category -> {
                        categoryPath.add(0, mapDomainObjectToResponse(category));
                        return category;
                    })
                    .map(Category::getParentCategoryId)
                    .orElse(null);

            if (categoryId == null) break;
        }

        Collections.reverse(categoryPath);

        return categoryPath;
    }
}
