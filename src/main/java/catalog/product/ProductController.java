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

        ProductDto productResponse = mapDomainObjectToResponse(savedProduct);

        return created(URI.create("/products/" + savedProduct.getId())).body(productResponse);
    }

    private Product mapRequestToDomainObject(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setCurrency(productRequest.getCurrency());
        product.setCategoryId(productRequest.getCategoryId());
        return product;
    }

    @RequestMapping(value = "{productId}", method = RequestMethod.PUT)
    public HttpEntity<ProductDto> updateProduct(
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
        return product;
    }

    @RequestMapping(value = "{productId}", method = RequestMethod.GET)
    public HttpEntity<ProductDto> getProduct(@PathVariable long productId) {
        return productRepository.findById(productId)
                .map(this::mapDomainObjectToResponse)
                .map(ResponseEntity::ok)
                .orElse(notFound().build());
    }

    private ProductDto mapDomainObjectToResponse(Product product) {
        return new ProductDto(product.getName(), product.getPrice(), product.getCurrency(), product.getCategoryId());
    }
}
