package catalog.category;

import lombok.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

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

        return created(URI.create("/categories/" + savedCategory.getId())).build();
    }

    private Category mapRequestToDomainObject(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.getName());
        return category;
    }

    @RequestMapping(value = "{categoryId}", method = RequestMethod.PUT)
    public HttpEntity<CategoryResponse> updateCategory(
            @PathVariable long categoryId,
            @RequestBody @Valid CategoryRequest categoryRequest
    ) {
        return categoryRepository.findById(categoryId)
                .map(category -> updateAttributes(category, categoryRequest))
                .map(categoryRepository::save)
                .map(this::mapDomainObjectToResponse)
                .map(ResponseEntity::ok)
                .orElse(notFound().build());
    }

    private Category updateAttributes(Category category, CategoryRequest categoryRequest) {
        category.setName(categoryRequest.getName());
        return category;
    }

    @RequestMapping(value = "{categoryId}", method = RequestMethod.GET)
    public HttpEntity<CategoryResponse> getCategory(@PathVariable long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(this::mapDomainObjectToResponse)
                .map(ResponseEntity::ok)
                .orElse(notFound().build());
    }

    private CategoryResponse mapDomainObjectToResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}
