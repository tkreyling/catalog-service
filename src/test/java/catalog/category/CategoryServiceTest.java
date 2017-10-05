package catalog.category;

import catalog.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Test
    public void theCatalogRetainsANewCategory() {
        CategoryResponse createResponse = createCategory("New Category", null);

        CategoryResponse categoryResponse = getCategory(createResponse.getId());

        assertEquals("New Category", categoryResponse.getName());
        assertNotEquals(0, categoryResponse.getId());
    }

    @Test
    public void anExistingCategoryCanBeUpdated() {
        CategoryResponse createResponse = createCategory("old name", null);

        CategoryResponse categoryResponse = updateCategory(createResponse.getId(), "new name");

        assertEquals("new name", categoryResponse.getName());
    }

    @Test
    public void aCategoryCanBeNestedInAnExistingCategory() {
        CategoryResponse existingCategory = createCategory("Existing Category", null);

        CategoryResponse newCategory = createCategory("New Category", existingCategory.getId());

        CategoryResponse reloadedExistingCategory = getCategory(existingCategory.getId());

        assertEquals(1, reloadedExistingCategory.getSubCategories().size());
    }

    private CategoryResponse createCategory(String name, Long parentCategoryId) {
        CategoryRequest createRequest = new CategoryRequest(name, parentCategoryId);

        CategoryResponse categoryResponse = categoryService.createCategory(createRequest);

        assertNotEquals(0, categoryResponse.getId());

        return categoryResponse;
    }

    private CategoryResponse updateCategory(long categoryId, String name) {
        CategoryRequest updateRequest = new CategoryRequest(name, null);

        return categoryService.updateCategory(categoryId, updateRequest).get();
    }

    private CategoryResponse getCategory(long categoryId) {
        return categoryService.loadCategory(categoryId).get();
    }
}