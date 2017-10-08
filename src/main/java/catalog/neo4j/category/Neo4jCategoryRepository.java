package catalog.neo4j.category;

import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface Neo4jCategoryRepository extends Neo4jRepository<Category, Long> {
}
