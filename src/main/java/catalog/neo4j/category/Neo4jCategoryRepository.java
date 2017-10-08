package catalog.neo4j.category;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Neo4jCategoryRepository extends Neo4jRepository<Category, Long> {

    @Query("MATCH (c:Category)-[:PARENT]->(p:Category) WHERE id(p)={id} return c")
    List<Category> findByParentId(@Param("id") Long id);

    @Query("MATCH (c:Category)-[:PARENT*]->(p:Category) WHERE id(c)={id} return p")
    Category findRootNode(@Param("id") Long id);

    @Query("MATCH path = (c:Category)-[:PARENT*]->(p:Category) WHERE id(c)={id} return path")
    List<Category> loadCategoryPath(@Param("id") Long id);

    List<Category> findByName(String name);
}
