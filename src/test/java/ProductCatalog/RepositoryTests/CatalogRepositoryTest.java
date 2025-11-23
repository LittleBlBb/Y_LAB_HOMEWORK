package ProductCatalog.RepositoryTests;

import ProductCatalog.Models.Catalog;
import ProductCatalog.Repositories.CatalogRepository;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CatalogRepositoryTest {

    private PostgreSQLContainer<?> postgres;
    private DataSource dataSource;
    private CatalogRepository catalogRepository;

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

        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE SCHEMA IF NOT EXISTS app;");
            stmt.execute("CREATE SEQUENCE IF NOT EXISTS app.catalog_seq START 1;");
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS app.catalog(
                        id BIGINT DEFAULT nextval('app.catalog_seq') PRIMARY KEY,
                        name VARCHAR(255) NOT NULL
                    );
                    """);
        }

        catalogRepository = new CatalogRepository(dataSource);
    }

    @AfterAll
    public void tearDown() throws Exception {
        if (postgres != null) {
            postgres.stop();
        }
    }

    @Test
    public void testInsertAndFind() {
        Catalog catalog = new Catalog("Test Catalog");
        catalogRepository.save(catalog);

        Catalog fetched = catalogRepository.findById(catalog.getId());
        Assertions.assertEquals(catalog.getName(), fetched.getName());
    }

    @Test
    public void testFindAll() {
        catalogRepository.save(new Catalog("Catalog 1"));
        catalogRepository.save(new Catalog("Catalog 2"));

        List<Catalog> all = catalogRepository.findAll();
        Assertions.assertTrue(all.size() == 2);
    }

    @Test
    public void testDelete() {
        Catalog catalog = catalogRepository.save(new Catalog("ToDelete"));
        boolean deleted = catalogRepository.delete(catalog.getId());
        Assertions.assertTrue(deleted);

        Catalog fetched = catalogRepository.findById(catalog.getId());
        Assertions.assertNull(fetched);
    }
}
