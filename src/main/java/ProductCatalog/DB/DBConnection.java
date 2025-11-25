package ProductCatalog.DB;

import org.postgresql.ds.PGSimpleDataSource;

public class DBConnection {
    private final PGSimpleDataSource dataSource;

    public DBConnection(Config config) {
        this.dataSource = new PGSimpleDataSource();

        String host = config.getProperty(ConfigKeys.DB_HOST);
        String portStr = config.getProperty(ConfigKeys.DB_PORT);
        String db = config.getProperty(ConfigKeys.DB_NAME);
        String user = config.getProperty(ConfigKeys.DB_USERNAME);
        String password = config.getProperty(ConfigKeys.DB_PASSWORD);
        String schema = config.getProperty(ConfigKeys.DB_SCHEMA);

        int port = Integer.parseInt(portStr);

        dataSource.setServerNames(new String[]{host});
        dataSource.setPortNumbers(new int[]{port});
        dataSource.setDatabaseName(db);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setCurrentSchema(schema);
        dataSource.setSsl(false);
    }

    public PGSimpleDataSource getDataSource() {
        return dataSource;
    }
}
