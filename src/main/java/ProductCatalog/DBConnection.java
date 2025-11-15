package ProductCatalog;

import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {
    private static PGSimpleDataSource dataSource;

    public static PGSimpleDataSource getDataSource() {
        if (dataSource == null) {
            dataSource = new PGSimpleDataSource();

            String host = System.getenv().getOrDefault("DB_HOST", "localhost");
            String portStr = System.getenv().getOrDefault("DB_PORT", "15432");
            String db = System.getenv().getOrDefault("DB_NAME", "postgres");
            String user = System.getenv().getOrDefault("DB_USER", "catalog");
            String password = System.getenv().getOrDefault("DB_PASSWORD", "password");
            String schema = System.getenv().getOrDefault("DB_SCHEMA", "app");

            int port = Integer.parseInt(portStr);

            dataSource.setServerNames(new String[]{host});
            dataSource.setPortNumbers(new int[]{port});
            dataSource.setDatabaseName(db);
            dataSource.setUser(user);
            dataSource.setPassword(password);
            dataSource.setCurrentSchema(schema);
            dataSource.setSsl(false);

        }
        return dataSource;
    }
}
