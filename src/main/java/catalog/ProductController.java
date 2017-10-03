package catalog;

import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.net.URI;

import static org.springframework.http.ResponseEntity.created;

@RestController
@RequestMapping("/products")
public class ProductController {

    @RequestMapping(method = RequestMethod.POST)
    public HttpEntity<?> createPoll(@RequestBody @Valid CreateProductDto createProductDto) {
        return created(URI.create("/products/1")).build();
    }
}
