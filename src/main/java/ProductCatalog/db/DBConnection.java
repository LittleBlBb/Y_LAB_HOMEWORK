package ProductCatalog.db;

import org.postgresql.ds.PGSimpleDataSource;

public class DBConnection {
    private static PGSimpleDataSource dataSource;

    public static PGSimpleDataSource getDataSource() {
        if (dataSource == null) {
            dataSource = new PGSimpleDataSource();

            Config config = Config.getInstance();

            String host = config.getDbHost();
            String portStr = config.getDbPort();
            String db = config.getDbName();
            String user = config.getDbUsername();
            String password = config.getDbPassword();
            String schema = config.getDbSchema();

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
