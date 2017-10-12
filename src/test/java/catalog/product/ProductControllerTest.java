package catalog.product;

import catalog.Application;
import catalog.category.CategoryEndpointMixin;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import lombok.Getter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class ProductControllerTest implements CategoryEndpointMixin {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Autowired
    @Getter
    private MockMvc mvc;

    @Autowired
    @Getter
    private ObjectMapper objectMapper;

    @Test
    public void theCatalogRejectsEmptyProductCreationRequest() throws Exception {
        mvc.perform(post("/products"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void theCatalogRetainsANewProduct() throws Exception {
        ProductRequest createRequest = productWithStandardPrice("New Product", null);

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
                MockMvcRequestBuilders.get(productUrl)
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        ProductResponse productResponse = objectMapper.readValue(getResponse.getContentAsString(), ProductResponse.class);
        assertEquals("New Product", productResponse.getName());
        assertEquals(new BigDecimal("100.00"), productResponse.getPrice());
        assertEquals("EUR", productResponse.getCurrency());
    }

    @Test
    public void forAProductWithANonEuroCurrencyTheCurrentPriceIsCalculated() throws Exception {
        stubFor(get(urlEqualTo("/latest?base=GBP&symbol=EUR"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("fixer.io.json")
                ));

        ProductRequest createRequest = new ProductRequest(
                "Product with GBP price",
                new BigDecimal("100.00"), "GBP",
                null
        );

        MockHttpServletResponse createResponse = mvc.perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createRequest))
        )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        ProductResponse productResponse = objectMapper.readValue(createResponse.getContentAsString(), ProductResponse.class);

        assertEquals(new BigDecimal("100.00"), productResponse.getPrice());
        assertEquals("GBP", productResponse.getCurrency());
        assertEquals(new BigDecimal("110.82"), productResponse.getPriceInEuro());
    }

    @Test
    public void anExistingProductCanBeUpdated() throws Exception {
        ProductRequest createRequest = productWithStandardPrice("old name", null);

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

        ProductRequest updateRequest = productWithStandardPrice("new name", null);

        MockHttpServletResponse updateResponse = mvc.perform(
                put(productUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(updateRequest))
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        ProductResponse productResponse = objectMapper.readValue(updateResponse.getContentAsString(), ProductResponse.class);
        assertEquals("new name", productResponse.getName());
    }

    @Test
    public void aProductCanBeLinkedToAnExistingCategory() throws Exception {
        CreateCategoryResult categoryResponse = createCategory("New Category", null);

        ProductRequest createRequest = productWithStandardPrice("New Product", categoryResponse.getResponse().getId());

        MockHttpServletResponse createResponse = mvc.perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createRequest))
        )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        ProductResponse productResponse = objectMapper.readValue(createResponse.getContentAsString(), ProductResponse.class);
        assertNotNull(productResponse.getCategoryId());
    }

    @Test
    public void aReferenceToAnNonExistingCategoryIsRejected() throws Exception {
        ProductRequest createRequest = productWithStandardPrice("New Product", 20000L);

        mvc.perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createRequest))
        )
                .andExpect(status().isBadRequest());
    }

    private ProductRequest productWithStandardPrice(String name, Long categoryId) {
        return new ProductRequest(name, new BigDecimal("100.00"), "EUR", categoryId);
    }
}