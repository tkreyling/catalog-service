package catalog.category;

import catalog.Application;
import catalog.product.ProductDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Value;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class CategoryControllerTest implements CategoryEndpointMixin {

    @Autowired
    @Getter
    private MockMvc mvc;

    @Autowired
    @Getter
    private ObjectMapper objectMapper;

    @Test
    public void theCatalogRejectsEmptyCategoryCreationRequest() throws Exception {
        mvc.perform(post("/categories"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void theCatalogRetainsANewCategory() throws Exception {
        CreateCategoryResult createResponse = createCategory("New Category", null);

        CategoryResponseWithSubCategories categoryResponse = getCategory(createResponse.getLocation());

        assertEquals("New Category", categoryResponse.getName());
        assertNotEquals(0, categoryResponse.getId());
    }

    @Test
    public void anExistingCategoryCanBeUpdated() throws Exception {
        CreateCategoryResult createResponse = createCategory("old name", null);

        CategoryResponseWithSubCategories categoryResponse = updateCategory(createResponse.getLocation(), "new name");

        assertEquals("new name", categoryResponse.getName());
    }

    @Test
    public void aCategoryCanBeNestedInAnExistingCategory() throws Exception {
        CreateCategoryResult existingCategory = createCategory("Existing Category", null);

        CreateCategoryResult newCategory = createCategory("New Category", existingCategory.getResponse().getId());

        CategoryResponseWithSubCategories reloadedExistingCategory = getCategory(existingCategory.getLocation());

        assertEquals(1, reloadedExistingCategory.getSubCategories().size());
    }

    @Test
    public void theCategoryPathIsTheListOfAllParentCategories() throws Exception {
        CreateCategoryResult root = createCategory("Root", null);
        CreateCategoryResult subCategory = createCategory("Sub Category", root.getResponse().getId());
        CreateCategoryResult subSubCategory = createCategory("Sub Sub Category", subCategory.getResponse().getId());

        List<CategoryResponse> categoryPath = getCategoryPath(subSubCategory.getResponse().getId());

        List<String> categoryNames = categoryPath.stream().map(CategoryResponse::getName).collect(toList());

        assertEquals(asList("Sub Sub Category", "Sub Category", "Root"), categoryNames);
    }

    @Test
    public void aReferenceToAnNonExistingCategoryIsRejected() throws Exception {
        CategoryRequest createRequest = new CategoryRequest("New Category", 20000L);

        getMvc().perform(
                post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(createRequest))
        )
                .andExpect(status().isBadRequest());
    }
}