package ProductCatalog;

import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class TestDBConnection {
    public static void main(String[] args) {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerNames(new String[]{"localhost"}); // <- вместо host.docker.internal
        ds.setPortNumbers(new int[]{5432});
        ds.setDatabaseName("catalog_db");
        ds.setUser("catalog");
        ds.setPassword("catalog");
        ds.setCurrentSchema("app");

        try (Connection conn = ds.getConnection()) {
            System.out.println("Connected!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
