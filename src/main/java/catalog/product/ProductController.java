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
    public HttpEntity<?> createProduct(@RequestBody @Valid ProductDto productDto) {
        Product product = mapRequestToDomainObject(productDto);

        Product savedProduct = productRepository.save(product);

        ProductDto productResponse = mapDomainObjectToResponse(savedProduct);

        return created(URI.create("/products/" + savedProduct.getId())).body(productResponse);
    }

    private Product mapRequestToDomainObject(ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setCategoryId(productDto.getCategoryId());
        return product;
    }

    @RequestMapping(value = "{productId}", method = RequestMethod.PUT)
    public HttpEntity<ProductDto> updateProduct(
            @PathVariable long productId,
            @RequestBody @Valid ProductDto productDto
    ) {
        return productRepository.findById(productId)
                .map(product -> updateAttributes(product, productDto))
                .map(productRepository::save)
                .map(this::mapDomainObjectToResponse)
                .map(ResponseEntity::ok)
                .orElse(notFound().build());
    }

    private Product updateAttributes(Product product, ProductDto productDto) {
        product.setName(productDto.getName());
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
        return new ProductDto(product.getName(), product.getCategoryId());
    }
}
