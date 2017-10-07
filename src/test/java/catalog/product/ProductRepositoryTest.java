package catalog.product;

import catalog.Application;
import catalog.category.Category;
import catalog.category.CategoryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void anEmptyRepositoryRetainsANewProduct() {
        Product newProduct = new Product();
        newProduct.setName("New Product");

        Product savedProduct = productRepository.save(newProduct);

        assertNotNull(savedProduct.getId());
    }

    @Test
    public void aProductCanBeLinkedToAnExistingCategory() {
        Category existingCategory = new Category();
        existingCategory.setName("Existing Category");

        categoryRepository.save(existingCategory);

        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setCategoryId(existingCategory.getId());

        Product savedProduct = productRepository.save(newProduct);

        assertNotNull(savedProduct.getId());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void aReferenceToAnNonExistingCategoryIsRejected() {
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setCategoryId(200000L);

        Product savedProduct = productRepository.save(newProduct);
    }
}