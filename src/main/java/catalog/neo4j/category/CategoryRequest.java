package catalog.neo4j.category;

import lombok.Value;

@Value
public class CategoryRequest {
    private String name;
    private Long parentCategoryId;
}
