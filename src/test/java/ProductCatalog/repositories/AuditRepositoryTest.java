package ProductCatalog.repositories;

import ProductCatalog.models.AuditEntry;
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
import java.time.LocalDateTime;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuditRepositoryTest {

    private PostgreSQLContainer<?> postgres;
    private DataSource dataSource;
    private AuditRepository auditRepository;

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
            stmt.execute("CREATE SEQUENCE IF NOT EXISTS app.audit_seq START 1;");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS app.audit_log(
                    id BIGINT DEFAULT nextval('app.audit_seq') PRIMARY KEY,
                    username VARCHAR(255),
                    action VARCHAR(255),
                    details TEXT,
                    timestamp TIMESTAMP
                );
                """);
        }

        auditRepository = new AuditRepository(dataSource);
    }

    @AfterAll
    public void tearDown() {
        if (postgres != null) postgres.stop();
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
