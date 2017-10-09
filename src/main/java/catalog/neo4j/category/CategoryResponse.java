package catalog.neo4j.category;

import lombok.Value;

@Value
public class CategoryResponse {
    private long id;
    private String name;
}
