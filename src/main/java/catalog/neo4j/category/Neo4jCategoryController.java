package catalog.neo4j.category;

import lombok.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static catalog.neo4j.category.Neo4jCategoryController.PATH;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping(PATH)
@Value
public class Neo4jCategoryController {
    public static final String PATH = "/neo4j/categories";

    private final Neo4jCategoryService categoryService;

    @RequestMapping(method = RequestMethod.POST)
    public HttpEntity<?> createCategory(
            @RequestBody @Valid CategoryRequest categoryRequest
    ) {
        CategoryResponse response = categoryService.createCategory(categoryRequest);
        return created(URI.create(PATH + "/" + response.getId())).body(response);
    }

    @RequestMapping(value = "{categoryId}", method = RequestMethod.PUT)
    public HttpEntity<CategoryResponseWithSubCategories> updateCategory(
            @PathVariable long categoryId,
            @RequestBody @Valid CategoryRequest categoryRequest
    ) {
        return categoryService.updateCategory(categoryId, categoryRequest)
                .map(ResponseEntity::ok)
                .orElse(notFound().build());
    }

    @RequestMapping(value = "{categoryId}", method = RequestMethod.GET)
    public HttpEntity<CategoryResponseWithSubCategories> getCategory(
            @PathVariable long categoryId
    ) {
        return categoryService.loadCategory(categoryId)
                .map(ResponseEntity::ok)
                .orElse(notFound().build());
    }

    @RequestMapping(value = "{categoryId}/path", method = RequestMethod.GET)
    public HttpEntity<List<CategoryResponse>> getCategoryPath(
            @PathVariable long categoryId
    ) {
        return ok(categoryService.loadCategoryPath(categoryId));
    }
}
