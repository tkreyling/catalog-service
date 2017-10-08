package catalog.neo4j.category;

import lombok.Data;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
@Data
public class Category {

    @GraphId
    private Long id;

    private String name;
}
