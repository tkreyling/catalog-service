package catalog.product;

import catalog.Application;
import catalog.category.CategoryEndpointMixin;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import lombok.Getter;
import lombok.Value;
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

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

        CreateProductResult createResponse = createProduct(createRequest);

        ProductResponse productResponse = getProduct(createResponse.getLocation());

        assertEquals("New Product", productResponse.getName());
        assertEquals(new BigDecimal("100.00"), productResponse.getPrice());
        assertEquals("EUR", productResponse.getCurrency());
    }

    @Test
    public void forAProductWithANonEuroCurrencyTheCurrentPriceIsCalculated() throws Exception {
        stubFor(WireMock.get(urlEqualTo("/latest?base=GBP&symbol=EUR"))
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

        CreateProductResult product = createProduct(createRequest);

        assertEquals(new BigDecimal("100.00"), product.getResponse().getPrice());
        assertEquals("GBP", product.getResponse().getCurrency());
        assertEquals(new BigDecimal("110.82"), product.getResponse().getPriceInEuro());
    }

    @Test
    public void anExistingProductCanBeUpdated() throws Exception {
        ProductRequest createRequest = productWithStandardPrice("old name", null);

        CreateProductResult existingProduct = createProduct(createRequest);

        ProductRequest updateRequest = productWithStandardPrice("new name", null);

        MockHttpServletResponse updateResponse = mvc.perform(
                put(existingProduct.getLocation())
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

        CreateProductResult product = createProduct(createRequest);

        assertNotNull(product.getResponse().getCategoryId());
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

    @Value
    class CreateProductResult {
        ProductResponse response;
        String location;
    }

    private CreateProductResult createProduct(ProductRequest createRequest) throws Exception {
        MockHttpServletResponse createResponse = mvc.perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createRequest))
        )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        ProductResponse productResponse = objectMapper.readValue(
                createResponse.getContentAsString(), ProductResponse.class);

        assertNotNull(createResponse.getHeader(LOCATION));
        assertNotEquals(0, productResponse.getId());

        return new CreateProductResult(productResponse, createResponse.getHeader(LOCATION));
    }

    private ProductResponse getProduct(String location) throws Exception {
        MockHttpServletResponse getResponse = mvc.perform(get(location))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        return objectMapper.readValue(getResponse.getContentAsString(), ProductResponse.class);
    }
}