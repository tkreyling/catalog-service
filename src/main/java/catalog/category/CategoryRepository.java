package catalog.category;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    List<Category> findByParentCategoryId(Long id);

    @Query(value = "WITH path(id, name, parent_category_id) AS (\n" +
            "    SELECT cb.id, cb.name, cb.parent_category_id FROM category cb WHERE id = :id\n" +
            "    UNION ALL\n" +
            "    SELECT ca.id, ca.name, ca.parent_category_id\n" +
            "    FROM path p INNER JOIN category ca ON p.parent_category_id = ca.id\n" +
            ")\n" +
            " SELECT id, name, parent_category_id FROM path WHERE name IS NOT NULL", nativeQuery = true)
    List<Category> loadCategoryPath(@Param("id") Long id);
}
