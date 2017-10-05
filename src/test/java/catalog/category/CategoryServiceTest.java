package catalog.category;

import catalog.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
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

        CategoryResponseWithSubCategories categoryResponse = getCategory(createResponse.getId());

        assertEquals("New Category", categoryResponse.getName());
        assertNotEquals(0, categoryResponse.getId());
    }

    @Test
    public void anExistingCategoryCanBeUpdated() {
        CategoryResponse createResponse = createCategory("old name", null);

        CategoryResponseWithSubCategories categoryResponse = updateCategory(createResponse.getId(), "new name");

        assertEquals("new name", categoryResponse.getName());
    }

    @Test
    public void aCategoryCanBeNestedInAnExistingCategory() {
        CategoryResponse existingCategory = createCategory("Existing Category", null);

        CategoryResponse newCategory = createCategory("New Category", existingCategory.getId());

        CategoryResponseWithSubCategories reloadedExistingCategory = getCategory(existingCategory.getId());

        assertEquals(1, reloadedExistingCategory.getSubCategories().size());
    }

    @Test
    public void theCategoryPathIsTheListOfAllParentCategories() {
        CategoryResponse root = createCategory("Root", null);
        CategoryResponse subCategory = createCategory("Sub Category", root.getId());
        CategoryResponse subSubCategory = createCategory("Sub Sub Category", subCategory.getId());

        List<CategoryResponse> categoryPath = categoryService.loadCategoryPath(subSubCategory.getId());

        List<String> categoryNames = categoryPath.stream().map(CategoryResponse::getName).collect(toList());

        assertEquals(asList("Root", "Sub Category", "Sub Sub Category"), categoryNames);
    }

    private CategoryResponse createCategory(String name, Long parentCategoryId) {
        CategoryRequest createRequest = new CategoryRequest(name, parentCategoryId);

        CategoryResponse categoryResponse = categoryService.createCategory(createRequest);

        assertNotEquals(0, categoryResponse.getId());

        return categoryResponse;
    }

    private CategoryResponseWithSubCategories updateCategory(long categoryId, String name) {
        CategoryRequest updateRequest = new CategoryRequest(name, null);

        return categoryService.updateCategory(categoryId, updateRequest).get();
    }

    private CategoryResponseWithSubCategories getCategory(long categoryId) {
        return categoryService.loadCategory(categoryId).get();
    }
}