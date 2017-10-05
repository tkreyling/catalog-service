package catalog.product;

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
public class ProductControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void theCatalogRejectsEmptyProductCreationRequest() throws Exception {
        mvc.perform(post("/products"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void theCatalogRetainsANewProduct() throws Exception {
        ProductDto createRequest = new ProductDto("New Product");

        MockHttpServletResponse createResponse = mvc.perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createRequest))
        )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        String productUrl = createResponse.getHeader(LOCATION);
        assertNotNull(productUrl);

        MockHttpServletResponse getResponse = mvc.perform(
                get(productUrl)
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        ProductDto productDto = objectMapper.readValue(getResponse.getContentAsString(), ProductDto.class);
        assertEquals("New Product", productDto.getName());
    }

    @Test
    public void anExistingProductCanBeUpdated() throws Exception {
        ProductDto createRequest = new ProductDto("old name");

        MockHttpServletResponse createResponse = mvc.perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createRequest))
        )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        String productUrl = createResponse.getHeader(LOCATION);
        assertNotNull(productUrl);

        ProductDto updateRequest = new ProductDto("new name");

        MockHttpServletResponse updateResponse = mvc.perform(
                put(productUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(updateRequest))
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        ProductDto productDto = objectMapper.readValue(updateResponse.getContentAsString(), ProductDto.class);
        assertEquals("new name", productDto.getName());
    }
}