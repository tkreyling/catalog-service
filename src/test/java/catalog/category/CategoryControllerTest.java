package catalog.category;

import catalog.Application;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.junit.Assert.*;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class CategoryControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void theCatalogRejectsEmptyCategoryCreationRequest() throws Exception {
        mvc.perform(post("/categories"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void theCatalogRetainsANewCategory() throws Exception {
        CreateCategoryResult createResponse = createCategory("New Category", null);

        assertNotNull(createResponse.location);

        CategoryResponse categoryResponse = getCategory(createResponse.location);

        assertEquals("New Category", categoryResponse.getName());
        assertNotEquals(0, categoryResponse.getId());
    }

    @Test
    public void anExistingCategoryCanBeUpdated() throws Exception {
        CreateCategoryResult createResponse = createCategory("old name", null);

        assertNotNull(createResponse.location);

        CategoryResponse categoryResponse = updateCategory(createResponse.location, "new name");

        assertEquals("new name", categoryResponse.getName());
    }

    @Test
    public void aCategoryCanBeNestedInAnExistingCategory() throws Exception {
        CreateCategoryResult existingCategory = createCategory("Existing Category", null);

        assertNotNull(existingCategory.location);
        assertNotEquals(0, existingCategory.response.getId());

        CreateCategoryResult newCategory = createCategory("New Category", existingCategory.response.getId());

        assertNotNull(newCategory.location);
        assertNotEquals(0, newCategory.response.getId());

        CategoryResponse reloadedExistingCategory = getCategory(existingCategory.location);

        assertEquals(1, reloadedExistingCategory.getSubCategories().size());
    }

    @Value
    private static class CreateCategoryResult {
        CategoryResponse response;
        String location;
    }

    private CreateCategoryResult createCategory(String name, Long parentCategoryId) throws Exception {
        CategoryRequest createRequest = new CategoryRequest(name, parentCategoryId);

        MockHttpServletResponse createResponse = mvc.perform(
                post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createRequest))
        )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        CategoryResponse categoryResponse = objectMapper.readValue(
                createResponse.getContentAsString(), CategoryResponse.class);

        return new CreateCategoryResult(categoryResponse, createResponse.getHeader(LOCATION));
    }

    private CategoryResponse updateCategory(String categoryUrl, String name) throws Exception {
        CategoryRequest updateRequest = new CategoryRequest(name, null);

        MockHttpServletResponse updateResponse = mvc.perform(
                put(categoryUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(updateRequest))
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        return objectMapper.readValue(updateResponse.getContentAsString(), CategoryResponse.class);
    }

    private CategoryResponse getCategory(String categoryUrl) throws Exception {
        MockHttpServletResponse getResponse = mvc.perform(
                get(categoryUrl)
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        return objectMapper.readValue(getResponse.getContentAsString(), CategoryResponse.class);
    }
}