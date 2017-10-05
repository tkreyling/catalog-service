package catalog.category;

import lombok.Value;

@Value
public class CategoryRequest {
    private String name;
    private Long parentCategoryId;
}
