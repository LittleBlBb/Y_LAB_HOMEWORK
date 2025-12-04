package ProductCatalog.DB;

public class ConfigKeys {
    private ConfigKeys() {}

    public static final String DB_HOST = "db.host";
    public static final String DB_USERNAME = "db.username";
    public static final String DB_PASSWORD = "db.password";
    public static final String DB_NAME = "db.name";
    public static final String DB_PORT = "db.port";
    public static final String DB_SCHEMA = "db.schema";

    public static final String LIQUIBASE_CHANGELOG = "liquibase.changeLog";
    public static final String LIQUIBASE_SERVICE_SCHEMA = "liquibase.serviceSchema";
}
