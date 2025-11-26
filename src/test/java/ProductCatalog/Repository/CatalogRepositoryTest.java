package ProductCatalog.Repository;

import ProductCatalog.Models.Catalog;
import ProductCatalog.Repositories.AuditRepository;
import ProductCatalog.Repositories.CatalogRepository;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CatalogRepositoryTest extends AbstractRepositoryTest{
    private CatalogRepository catalogRepository;

    @BeforeEach
    void init() {
        catalogRepository = new CatalogRepository(dataSource);
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
        Assertions.assertTrue(all.size() == 6);
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