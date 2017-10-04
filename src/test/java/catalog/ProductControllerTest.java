package catalog;

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
        CreateProductDto createRequest = new CreateProductDto("New Product");

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
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse();

        GetProductDto getProductDto = objectMapper.readValue(getResponse.getContentAsString(), GetProductDto.class);
        assertEquals("New Product", getProductDto.getName());
    }
}