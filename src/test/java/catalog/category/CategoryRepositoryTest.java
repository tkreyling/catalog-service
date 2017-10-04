package catalog.category;

import catalog.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void anEmptyRepositoryRetainsANewCategory() {
        Category newCategory = new Category();
        newCategory.setName("New Category");

        Category savedCategory = categoryRepository.save(newCategory);

        assertNotNull(savedCategory.getId());
    }

    @Test
    public void aCategoryCanBeNestedInAnExistingCategory() {
        Category existingCategory = new Category();
        existingCategory.setName("Existing Category");

        existingCategory = categoryRepository.save(existingCategory);

        Category newCategory = new Category();
        newCategory.setName("New Category");
        newCategory.setParentCategory(existingCategory);

        Category savedCategory = categoryRepository.save(newCategory);

        assertNotNull(savedCategory.getId());
        assertNotNull(savedCategory.getParentCategory().getId());
        assertEquals("Existing Category", savedCategory.getParentCategory().getName());
    }

}