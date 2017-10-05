package catalog.category;

import catalog.Application;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
        CategoryDto createRequest = new CategoryDto("New Category");

        MockHttpServletResponse createResponse = mvc.perform(
                post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createRequest))
        )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        String categoryUrl = createResponse.getHeader(LOCATION);
        assertNotNull(categoryUrl);

        MockHttpServletResponse getResponse = mvc.perform(
                get(categoryUrl)
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        CategoryResponse categoryResponse = objectMapper.readValue(
                getResponse.getContentAsString(), CategoryResponse.class);
        assertEquals("New Category", categoryResponse.getName());
    }

    @Test
    public void anExistingCategoryCanBeUpdated() throws Exception {
        CategoryDto createRequest = new CategoryDto("old name");

        MockHttpServletResponse createResponse = mvc.perform(
                post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createRequest))
        )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        String categoryUrl = createResponse.getHeader(LOCATION);
        assertNotNull(categoryUrl);

        CategoryDto updateRequest = new CategoryDto("new name");

        MockHttpServletResponse updateResponse = mvc.perform(
                put(categoryUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(updateRequest))
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        CategoryResponse categoryResponse = objectMapper.readValue(
                updateResponse.getContentAsString(), CategoryResponse.class);
        assertEquals("new name", categoryResponse.getName());
    }
}