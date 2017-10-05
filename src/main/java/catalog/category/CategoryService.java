package catalog.category;

import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Service
@Value
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Category category = mapRequestToDomainObject(categoryRequest);

        Category savedCategory = categoryRepository.save(category);

        return mapDomainObjectToResponse(savedCategory, emptyList());
    }

    private Category mapRequestToDomainObject(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setParentCategoryId(categoryRequest.getParentCategoryId());
        return category;
    }

    public Optional<CategoryResponse> updateCategory(long categoryId, CategoryRequest categoryRequest) {
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

    public Optional<CategoryResponse> loadCategory(long categoryId) {
        List<Category> subCategories = categoryRepository.findByParentCategoryId(categoryId);
        return categoryRepository.findById(categoryId)
                .map(category -> mapDomainObjectToResponse(category, subCategories));
    }

    private CategoryResponse mapDomainObjectToResponse(Category category, List<Category> subCategories) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                subCategories.stream()
                        .map(subCategory -> mapDomainObjectToResponse(subCategory, emptyList()))
                        .collect(toList())
        );
    }
}
