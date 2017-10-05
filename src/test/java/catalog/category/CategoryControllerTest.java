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
        CreateCategoryResult createResponse = createCategory("New Category");

        assertNotNull(createResponse.location);

        CategoryResponse categoryResponse = getCategory(createResponse.location);

        assertEquals("New Category", categoryResponse.getName());
        assertNotEquals(0, categoryResponse.getId());
    }

    @Test
    public void anExistingCategoryCanBeUpdated() throws Exception {
        CreateCategoryResult createResponse = createCategory("old name");

        assertNotNull(createResponse.location);

        CategoryResponse categoryResponse = updateCategory(createResponse.location, "new name");

        assertEquals("new name", categoryResponse.getName());
    }

    @Test
    public void aCategoryCanBeNestedInAnExistingCategory() throws Exception {
        CreateCategoryResult createResponse = createCategory("Existing Category");

        assertNotNull(createResponse.location);
        assertNotEquals(0, createResponse.response.getId());

    }

    @Value
    private static class CreateCategoryResult {
        CategoryResponse response;
        String location;
    }

    private CreateCategoryResult createCategory(String name) throws Exception {
        CategoryRequest createRequest = new CategoryRequest(name);

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
        CategoryRequest updateRequest = new CategoryRequest(name);

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