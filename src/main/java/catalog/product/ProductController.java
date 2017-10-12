package catalog.product;

import catalog.exchangerates.ExchangeRateService;
import lombok.Value;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;

import static java.math.RoundingMode.HALF_DOWN;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.notFound;

@RestController
@RequestMapping("/products")
@Value
public class ProductController {
    private final ProductRepository productRepository;

    private final ExchangeRateService exchangeRateService;

    @RequestMapping(method = RequestMethod.POST)
    public HttpEntity<?> createProduct(@RequestBody @Valid ProductRequest productRequest) {
        return Optional.of(productRequest)
                .map(this::mapRequestToDomainObject)
                .map(productRepository::save)
                .map(this::readExchangeRateToEuroForProduct)
                .map(this::mapDomainObjectToResponse)
                .map(response -> created(URI.create("/products/" + response.getId())).body(response))
                .get();
    }

    private Product mapRequestToDomainObject(ProductRequest productRequest) {
        return updateAttributes(new Product(), productRequest);
    }

    @RequestMapping(value = "{productId}", method = RequestMethod.PUT)
    public HttpEntity<ProductResponse> updateProduct(
            @PathVariable long productId,
            @RequestBody @Valid ProductRequest productRequest
    ) {
        return productRepository.findById(productId)
                .map(product -> updateAttributes(product, productRequest))
                .map(productRepository::save)
                .map(this::readExchangeRateToEuroForProduct)
                .map(this::mapDomainObjectToResponse)
                .map(ResponseEntity::ok)
                .orElse(notFound().build());
    }

    private Product updateAttributes(Product product, ProductRequest productRequest) {
        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setCurrency(productRequest.getCurrency());
        product.setCategoryId(productRequest.getCategoryId());
        return product;
    }

    @RequestMapping(value = "{productId}", method = RequestMethod.GET)
    public HttpEntity<ProductResponse> getProduct(@PathVariable long productId) {
        return productRepository.findById(productId)
                .map(this::readExchangeRateToEuroForProduct)
                .map(this::mapDomainObjectToResponse)
                .map(ResponseEntity::ok)
                .orElse(notFound().build());
    }

    private Pair<Product, BigDecimal> readExchangeRateToEuroForProduct(Product product) {
        return Pair.of(product, exchangeRateService.getLatestExchangeRateToEuro(product.getCurrency()));
    }

    private ProductResponse mapDomainObjectToResponse(Pair<Product, BigDecimal> productAndExchangeRate) {
        Product product = productAndExchangeRate.getLeft();
        BigDecimal exchangeRate = productAndExchangeRate.getRight();
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCurrency(),
                product.getPrice().multiply(exchangeRate).setScale(2, HALF_DOWN),
                product.getCategoryId()
        );
    }
}
