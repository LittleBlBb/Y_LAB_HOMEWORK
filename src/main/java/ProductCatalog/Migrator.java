package ProductCatalog;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.SQLException;

public class Migrator {
    public static void main(String[] args) {
        try (Connection connection = DBConnection.getDataSource().getConnection()){
            connection.createStatement().execute("SET search_path TO app, public");
            Database database =
                    DatabaseFactory
                            .getInstance()
                            .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setDefaultSchemaName("app");
            Liquibase liquibase = new Liquibase(
                    "db/changelog/changelog.xml",
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
