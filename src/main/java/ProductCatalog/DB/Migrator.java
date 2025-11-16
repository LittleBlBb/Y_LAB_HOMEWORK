package ProductCatalog.DB;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Migrator {
    public static void main(String[] args) {
        try (Connection connection = DBConnection.getDataSource().getConnection()){
            connection.createStatement().execute("CREATE SCHEMA IF NOT EXISTS app;");
            connection.createStatement().execute("CREATE SCHEMA IF NOT EXISTS liquibase;");
            connection.createStatement().execute("SET search_path TO app, public");
            Database database =
                    DatabaseFactory
                            .getInstance()
                            .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setDefaultSchemaName(Config.getInstance().getDbSchema());
            database.setLiquibaseSchemaName(Config.getInstance().getLiquibaseServiceSchema());
            Liquibase liquibase = new Liquibase(
                    Config.getInstance().getLiquibaseChangeLog(),
                    new ClassLoaderResourceAccessor(),
                    database
            );
            liquibase.update("");
            System.out.println("Migration is completed successfully");
        } catch (SQLException | LiquibaseException e) {
            System.out.println("Exception in migration: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
