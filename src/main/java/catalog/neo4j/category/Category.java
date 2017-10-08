package catalog.neo4j.category;

import lombok.Data;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
@Data
public class Category {

    @GraphId
    private Long id;

    private String name;

    @Relationship(type = "PARENT")
    private Category parent;
}
