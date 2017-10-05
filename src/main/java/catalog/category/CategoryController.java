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
    public HttpEntity<?> createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        Category category = mapRequestToDomainObject(categoryDto);

        Category savedCategory = categoryRepository.save(category);

        return created(URI.create("/categories/" + savedCategory.getId())).build();
    }

    private Category mapRequestToDomainObject(CategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        return category;
    }

    @RequestMapping(value = "{categoryId}", method = RequestMethod.PUT)
    public HttpEntity<CategoryDto> updateCategory(
            @PathVariable long categoryId,
            @RequestBody @Valid CategoryDto categoryDto
    ) {
        return categoryRepository.findById(categoryId)
                .map(category -> updateAttributes(category, categoryDto))
                .map(categoryRepository::save)
                .map(this::mapDomainObjectToResponse)
                .map(ResponseEntity::ok)
                .orElse(notFound().build());
    }

    private Category updateAttributes(Category category, CategoryDto categoryDto) {
        category.setName(categoryDto.getName());
        return category;
    }

    @RequestMapping(value = "{categoryId}", method = RequestMethod.GET)
    public HttpEntity<CategoryDto> getCategory(@PathVariable long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(this::mapDomainObjectToResponse)
                .map(ResponseEntity::ok)
                .orElse(notFound().build());
    }

    private CategoryDto mapDomainObjectToResponse(Category category) {
        return new CategoryDto(category.getName());
    }
}
