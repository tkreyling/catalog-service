package catalog.neo4j.category;

import lombok.Value;

import java.util.List;

@Value
public class CategoryResponseWithSubCategories {
    private long id;
    private String name;
    private List<CategoryResponse> subCategories;
}
