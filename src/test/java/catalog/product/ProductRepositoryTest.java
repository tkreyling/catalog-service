package catalog.product;

import catalog.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    public void anEmptyRepositoryRetainsANewProduct() {
        Product newProduct = new Product();
        newProduct.setName("New Product");

        Product savedProduct = productRepository.save(newProduct);
        long productCount = productRepository.count();

        assertNotNull(savedProduct.getId());
        assertEquals(1, productCount);
    }
}