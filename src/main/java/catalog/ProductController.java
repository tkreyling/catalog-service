package catalog;

import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/products")
public class ProductController {

    @RequestMapping(method = RequestMethod.GET)
    public HttpEntity<?> createPoll() {
        return ok("Hello World!");
    }
}
