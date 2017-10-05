package catalog.category;

import lombok.Value;

import java.util.List;

@Value
public class CategoryResponse {
    private long id;
    private String name;
    private List<CategoryResponse> subCategories;
}
