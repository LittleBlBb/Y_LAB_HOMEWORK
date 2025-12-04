package ProductCatalog.repositories;

import ProductCatalog.models.Role;
import ProductCatalog.models.User;
import ProductCatalog.repositories.implemetations.UserRepository;
import org.junit.jupiter.api.*;

import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserRepositoryTest {

    private PostgreSQLContainer<?> postgres;
    private DataSource dataSource;
    private UserRepository userRepository;

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
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS app.user(
                        id SERIAL PRIMARY KEY,
                        username VARCHAR(255) NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        role VARCHAR(50) NOT NULL
                    );
                    """);
        }

        userRepository = new UserRepository(dataSource);
    }

    @AfterAll
    public void tearDown() throws Exception {
        if (postgres != null) {
            postgres.stop();
        }
    }

    @Test
    public void testInsertAndFind() {
        User savedUser = new User("testUser", "testPassword", Role.USER);
        userRepository.save(savedUser);

        User fetchedUser = userRepository.findById(savedUser.getId());

        Assertions.assertArrayEquals(
                new String[]{"testUser", "testPassword", Role.USER.toString()},
                new String[]{fetchedUser.getUsername(), fetchedUser.getPassword(), fetchedUser.getRole().toString()}
        );
    }
}
