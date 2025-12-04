package ProductCatalog.repositories;

import ProductCatalog.models.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductRepositoryTest {
    private PostgreSQLContainer<?> postgres;
    private DataSource dataSource;
    private ProductRepository productRepository;

    @BeforeAll
    public void setUp() throws Exception {
        postgres = new PostgreSQLContainer<>("postgres:16")
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testpass");
        postgres.start();

        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerNames(new String[]{postgres.getHost()});
        ds.setPortNumbers(new int[]{postgres.getFirstMappedPort()});
        ds.setDatabaseName(postgres.getDatabaseName());
        ds.setUser(postgres.getUsername());
        ds.setPassword(postgres.getPassword());
        ds.setCurrentSchema("app");
        dataSource = ds;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()){

            statement.execute("CREATE SCHEMA IF NOT EXISTS app;");

            statement.execute("CREATE SEQUENCE IF NOT EXISTS app.product_seq START 1;");
            statement.execute("CREATE SEQUENCE IF NOT EXISTS app.catalog_seq START 1;");

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS app.catalog(
                    id BIGINT DEFAULT nextval('app.catalog_seq') PRIMARY KEY,
                    name VARCHAR(255) NOT NULL
                    );
                    """);
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS app.product(
                    id BIGINT DEFAULT nextval('app.product_seq') PRIMARY KEY,
                    catalog_id BIGINT NOT NULL,
                    name VARCHAR(255) NOT NULL,
                    price NUMERIC(12,2),
                    brand VARCHAR(255),
                    category VARCHAR(255),
                    description Text
                    );
                    """);


            statement.execute("""
                    ALTER TABLE app.product
                    ADD Constraint fk_product_catalog
                    FOREIGN KEY (catalog_id)
                    REFERENCES app.catalog(id);
                    """);

            productRepository = new ProductRepository(dataSource);
        }
        try(Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()){
            statement.execute("INSERT INTO app.catalog(name) VALUES ('Default Catalog');");
        }
    }

    @AfterAll
    public void tearDown(){
        if (postgres != null) postgres.stop();
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

        Assertions.assertTrue(list.size() == 2);
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