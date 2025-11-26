package ProductCatalog.Repository;

import ProductCatalog.DB.Config;
import ProductCatalog.DB.ConfigKeys;
import ProductCatalog.DB.DBConnection;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractRepositoryTest {
    protected PostgreSQLContainer<?> postgres;
    protected DataSource dataSource;

    @BeforeAll
    public void setUp() throws Exception {
        postgres = new PostgreSQLContainer<>("postgres:16")
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testpass");
        postgres.start();

        Config config = new Config();
        config.override(ConfigKeys.DB_HOST, postgres.getHost());
        config.override(ConfigKeys.DB_PORT, String.valueOf(postgres.getFirstMappedPort()));
        config.override(ConfigKeys.DB_NAME, postgres.getDatabaseName());
        config.override(ConfigKeys.DB_USERNAME, postgres.getUsername());
        config.override(ConfigKeys.DB_PASSWORD, postgres.getPassword());

        DBConnection dbConnection = new DBConnection(config);
        dataSource = dbConnection.getDataSource();

        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE SCHEMA IF NOT EXISTS app;");
            stmt.execute("CREATE SCHEMA IF NOT EXISTS liquibase;");
        }

        runMigrations(config);

        initSchema();
    }

    private void runMigrations(Config config) throws Exception {
        try(Connection conn = dataSource.getConnection()) {

            Database database = DatabaseFactory
                    .getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(conn));

            database.setDefaultSchemaName(config.getProperty(ConfigKeys.DB_SCHEMA));
            database.setLiquibaseSchemaName(config.getProperty(ConfigKeys.LIQUIBASE_SERVICE_SCHEMA));

            Liquibase liquibase = new Liquibase(
                    config.getProperty(ConfigKeys.LIQUIBASE_CHANGELOG),
                    new ClassLoaderResourceAccessor(),
                    database
            );

            liquibase.update("");
        }
    }

    protected void initSchema() throws Exception {};

    @AfterAll
    public void tearDown() throws Exception {
        if (postgres != null) {
            postgres.stop();
        }
    }
}
