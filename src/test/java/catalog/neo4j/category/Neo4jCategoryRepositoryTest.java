package catalog.neo4j.category;

import catalog.Application;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@Transactional
public class Neo4jCategoryRepositoryTest {
    @Autowired
    private Session session;

    @Autowired
    private Neo4jCategoryRepository neo4jCategoryRepository;

    @Test
    public void anEmptyRepositoryRetainsANewCategory() {
        Category category = new Category();
        category.setName("New Category");

        Category savedCategory = neo4jCategoryRepository.save(category);

        assertNotNull(savedCategory.getId());
    }

    @Test
    public void aCategoryCanBeNestedInAnExistingCategory() {
        Category existingCategory = new Category();
        existingCategory.setName("Existing Category");

        neo4jCategoryRepository.save(existingCategory);

        Category newCategory = new Category();
        newCategory.setName("New Category");
        newCategory.setParent(existingCategory);

        neo4jCategoryRepository.save(newCategory);

        List<Category> subCategories = neo4jCategoryRepository.findByParentId(existingCategory.getId());

        assertEquals(1, subCategories.size());
        assertEquals("New Category", subCategories.get(0).getName());
    }

    @After
    public void tearDown() {
        session.purgeDatabase();
    }
}