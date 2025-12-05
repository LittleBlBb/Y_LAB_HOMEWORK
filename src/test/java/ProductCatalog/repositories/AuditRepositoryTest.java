package ProductCatalog.repositories;

import ProductCatalog.models.AuditEntry;
import ProductCatalog.repositories.implemetations.AuditRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDateTime;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuditRepositoryTest extends AbstractRepositoryTest {

    private AuditRepository auditRepository;

    @BeforeEach
    void init() {
        auditRepository = new AuditRepository(dataSource);
    }

    @Test
    public void testInsertAndFind() {
        AuditEntry a = new AuditEntry(
                "john",
                "CREATE_PRODUCT",
                "Created new phone",
                LocalDateTime.now()
        );

        auditRepository.save(a);

        AuditEntry fetched = auditRepository.findById(a.getId());
        Assertions.assertEquals(a.getAction(), fetched.getAction());
        Assertions.assertEquals(a.getUsername(), fetched.getUsername());
    }

    @Test
    public void testFindAll() {
        auditRepository.save(new AuditEntry("u1", "A1", "d1", LocalDateTime.now()));
        auditRepository.save(new AuditEntry("u2", "A2", "d2", LocalDateTime.now()));

        List<AuditEntry> list = auditRepository.findAll();
        Assertions.assertTrue(list.size() == 2);
    }
}
