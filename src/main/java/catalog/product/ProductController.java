package catalog.product;

import lombok.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.notFound;

@RestController
@RequestMapping("/products")
@Value
public class ProductController {
    private final ProductRepository productRepository;

    @RequestMapping(method = RequestMethod.POST)
    public HttpEntity<?> createProduct(@RequestBody @Valid ProductRequest productRequest) {
        Product product = mapRequestToDomainObject(productRequest);

        Product savedProduct = productRepository.save(product);

        ProductResponse productResponse = mapDomainObjectToResponse(savedProduct);

        return created(URI.create("/products/" + savedProduct.getId())).body(productResponse);
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
                .map(this::mapDomainObjectToResponse)
                .map(ResponseEntity::ok)
                .orElse(notFound().build());
    }

    private ProductResponse mapDomainObjectToResponse(Product product) {
        return new ProductResponse(product.getName(), product.getPrice(), product.getCurrency(), product.getCategoryId());
    }
}
