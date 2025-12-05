package ProductCatalog.db;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.SQLException;

public class Migrator {
    private final Config config;
    private final DBConnection dbConnection;

    public Migrator(Config config, DBConnection dbConnection) {
        this.config = config;
        this.dbConnection = dbConnection;
    }

    public void migrate() throws SQLException, LiquibaseException {
        try (Connection connection = dbConnection.getDataSource().getConnection()){
            connection.createStatement().execute("CREATE SCHEMA IF NOT EXISTS app;");
            connection.createStatement().execute("CREATE SCHEMA IF NOT EXISTS liquibase;");
            connection.createStatement().execute("SET search_path TO app, public");
            Database database =
                    DatabaseFactory
                            .getInstance()
                            .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setDefaultSchemaName(config.getProperty(ConfigKeys.DB_SCHEMA));
            database.setLiquibaseSchemaName(config.getProperty(ConfigKeys.LIQUIBASE_SERVICE_SCHEMA));
            Liquibase liquibase = new Liquibase(
                    config.getProperty(ConfigKeys.LIQUIBASE_CHANGELOG),
                    new ClassLoaderResourceAccessor(),
                    database
            );
            liquibase.update("");
            System.out.println("Migration is completed successfully");
        }
    }
}
