package catalog.category;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public interface CategoryEndpointMixin {
    MockMvc getMvc();

    ObjectMapper getObjectMapper();

    @Value
    class CreateCategoryResult {
        CategoryResponse response;
        String location;
    }

    default CreateCategoryResult createCategory(String name, Long parentCategoryId) throws Exception {
        CategoryRequest createRequest = new CategoryRequest(name, parentCategoryId);

        MockHttpServletResponse createResponse = getMvc().perform(
                post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(createRequest))
        )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        CategoryResponse categoryResponse = getObjectMapper().readValue(
                createResponse.getContentAsString(), CategoryResponse.class);

        assertNotNull(createResponse.getHeader(LOCATION));
        assertNotEquals(0, categoryResponse.getId());

        return new CreateCategoryResult(categoryResponse, createResponse.getHeader(LOCATION));
    }

    default CategoryResponseWithSubCategories updateCategory(String categoryUrl, String name) throws Exception {
        CategoryRequest updateRequest = new CategoryRequest(name, null);

        MockHttpServletResponse updateResponse = getMvc().perform(
                put(categoryUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(updateRequest))
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        return getObjectMapper().readValue(updateResponse.getContentAsString(), CategoryResponseWithSubCategories.class);
    }

    default CategoryResponseWithSubCategories getCategory(String categoryUrl) throws Exception {
        MockHttpServletResponse getResponse = getMvc().perform(
                get(categoryUrl)
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        return getObjectMapper().readValue(getResponse.getContentAsString(), CategoryResponseWithSubCategories.class);
    }

    default List<CategoryResponse> getCategoryPath(long categoryId) throws Exception {
        MockHttpServletResponse getResponse = getMvc().perform(
                get("/categories/" + categoryId + "/path")
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        return getObjectMapper().readValue(getResponse.getContentAsString(), new TypeReference<List<CategoryResponse>>() {
        });
    }
}
