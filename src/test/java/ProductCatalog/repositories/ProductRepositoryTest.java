package ProductCatalog.repositories;

import ProductCatalog.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductRepositoryTest extends AbstractRepositoryTest{

    private ProductRepository productRepository;

    @BeforeEach
    void init() {
        productRepository = new ProductRepository(dataSource);
    }

    @Test
    public void testInsertAndFind(){
        Product product = new Product(
                1L,
                "Phone",
                799,
                "BrandGood",
                "Electronics",
                "Smartphone"
        );

        productRepository.save(product);

        Product fetched = productRepository.findById(product.getId());

        Assertions.assertEquals("Phone", fetched.getName());
        Assertions.assertEquals("BrandGood", fetched.getBrand());
        Assertions.assertEquals("Smartphone", fetched.getDescription());
    }

    @Test
    public void testFindAll(){
        Product product1 = new Product(1L, "product1", 10, "description1", "brand1", "cat1");
        Product product2 = new Product(1L, "product2", 20, "description2", "brand2", "cat2");

        productRepository.save(product1);
        productRepository.save(product2);

        List<Product> list = productRepository.findAll();

        Assertions.assertTrue(list.size() == 14);
    }

    @Test
    public void testDelete() {
        Product product = new Product(1L, "toDelete", 1, null, null, null);
        productRepository.save(product);

        boolean ok = productRepository.delete(product.getId());
        Assertions.assertTrue(ok);

        Product fetched = productRepository.findById(product.getId());
        Assertions.assertNull(fetched);
    }
}