package ProductCatalog.db;

import org.postgresql.ds.PGSimpleDataSource;

public class DBConnection {
    private final PGSimpleDataSource dataSource;

    public DBConnection(Config config) {
        this(
                config.getProperty(ConfigKeys.DB_HOST),
                Integer.parseInt(config.getProperty(ConfigKeys.DB_PORT)),
                config.getProperty(ConfigKeys.DB_NAME),
                config.getProperty(ConfigKeys.DB_USERNAME),
                config.getProperty(ConfigKeys.DB_PASSWORD),
                config.getProperty(ConfigKeys.DB_SCHEMA)
        );
    }

    public DBConnection(String host, int port, String db, String user, String password, String schema) {
        this.dataSource = new PGSimpleDataSource();
        dataSource.setServerNames(new String[]{host});
        dataSource.setPortNumbers(new int[]{port});
        dataSource.setDatabaseName(db);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setCurrentSchema(schema);
    }

    public PGSimpleDataSource getDataSource() {
        return dataSource;
    }
}
