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
        long categoryCount = categoryRepository.count();

        assertNotNull(savedCategory.getId());
        assertEquals(1, categoryCount);
    }

}