package catalog.category;

import lombok.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.notFound;

@RestController
@RequestMapping("/categories")
@Value
public class CategoryController {
    private final CategoryRepository categoryRepository;

    @RequestMapping(method = RequestMethod.POST)
    public HttpEntity<?> createCategory(@RequestBody @Valid CategoryRequest categoryRequest) {
        Category category = mapRequestToDomainObject(categoryRequest);

        Category savedCategory = categoryRepository.save(category);

        return created(URI.create("/categories/" + savedCategory.getId()))
                .body(mapDomainObjectToResponse(savedCategory, emptyList()));
    }

    private Category mapRequestToDomainObject(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setParentCategoryId(categoryRequest.getParentCategoryId());
        return category;
    }

    @RequestMapping(value = "{categoryId}", method = RequestMethod.PUT)
    public HttpEntity<CategoryResponse> updateCategory(
            @PathVariable long categoryId,
            @RequestBody @Valid CategoryRequest categoryRequest
    ) {
        List<Category> subCategories = categoryRepository.findByParentCategoryId(categoryId);
        return categoryRepository.findById(categoryId)
                .map(category -> updateAttributes(category, categoryRequest))
                .map(categoryRepository::save)
                .map(category -> mapDomainObjectToResponse(category, subCategories))
                .map(ResponseEntity::ok)
                .orElse(notFound().build());
    }

    private Category updateAttributes(Category category, CategoryRequest categoryRequest) {
        category.setName(categoryRequest.getName());
        return category;
    }

    @RequestMapping(value = "{categoryId}", method = RequestMethod.GET)
    public HttpEntity<CategoryResponse> getCategory(@PathVariable long categoryId) {
        List<Category> subCategories = categoryRepository.findByParentCategoryId(categoryId);
        return categoryRepository.findById(categoryId)
                .map(category -> mapDomainObjectToResponse(category, subCategories))
                .map(ResponseEntity::ok)
                .orElse(notFound().build());
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
