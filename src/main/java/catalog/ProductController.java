package catalog;

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
    public HttpEntity<?> createProduct(@RequestBody @Valid CreateProductDto createProductDto) {
        Product product = mapRequestToDomainObject(createProductDto);

        Product savedProduct = productRepository.save(product);

        return created(URI.create("/products/" + savedProduct.getId())).build();
    }

    private Product mapRequestToDomainObject(CreateProductDto createProductDto) {
        Product product = new Product();
        product.setName(createProductDto.getName());
        return product;
    }

    @RequestMapping(value = "{productId}", method = RequestMethod.GET)
    public HttpEntity<GetProductDto> getProduct(@PathVariable long productId) {
        return productRepository.findById(productId)
                .map(this::mapDomainObjectToResponse)
                .map(ResponseEntity::ok)
                .orElse(notFound().build());
    }

    private GetProductDto mapDomainObjectToResponse(Product product) {
        return new GetProductDto(product.getName());
    }
}
