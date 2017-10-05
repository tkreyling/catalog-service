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
    private final CategoryService categoryService;

    @RequestMapping(method = RequestMethod.POST)
    public HttpEntity<?> createCategory(
            @RequestBody @Valid CategoryRequest categoryRequest
    ) {
        CategoryResponse response = categoryService.createCategory(categoryRequest);
        return created(URI.create("/categories/" + response.getId())).body(response);
    }

    @RequestMapping(value = "{categoryId}", method = RequestMethod.PUT)
    public HttpEntity<CategoryResponse> updateCategory(
            @PathVariable long categoryId,
            @RequestBody @Valid CategoryRequest categoryRequest
    ) {
        return categoryService.updateCategory(categoryId, categoryRequest)
                .map(ResponseEntity::ok)
                .orElse(notFound().build());
    }

    @RequestMapping(value = "{categoryId}", method = RequestMethod.GET)
    public HttpEntity<CategoryResponse> getCategory(
            @PathVariable long categoryId
    ) {
        return categoryService.loadCategory(categoryId)
                .map(ResponseEntity::ok)
                .orElse(notFound().build());
    }
}
