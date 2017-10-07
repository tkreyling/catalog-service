package catalog.category;

import catalog.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

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

        categoryRepository.save(existingCategory);

        Category newCategory = new Category();
        newCategory.setName("New Category");
        newCategory.setParentCategoryId(existingCategory.getId());

        categoryRepository.save(newCategory);

        List<Category> subCategories = categoryRepository.findByParentCategoryId(existingCategory.getId());

        assertEquals(1, subCategories.size());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void aReferenceToAnNonExistingCategoryIsRejected() {
        Category newCategory = new Category();
        newCategory.setName("New Category");
        newCategory.setParentCategoryId(2000000L);

        categoryRepository.save(newCategory);
    }

}