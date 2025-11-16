package ProductCatalog.DB;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private final Properties properties = new Properties();
    private static Config instance;
    private Config(){
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties"))
        {
            if (input == null){
                System.out.println("application.properties was not found");
            }
            properties.load(input);
        } catch (IOException e){
            System.out.println("Error in load config");
        }
    }
    public static Config getInstance(){
        if (instance == null){
            instance = new Config();
        }
        return instance;
    }

    public String getProperty(String key){
        return properties.getProperty(key);
    }

    public String getDbHost(){
        return getProperty("db.host");
    }

    public String getDbUsername() {
        return getProperty("db.username");
    }

    public String getDbPassword() {
        return getProperty("db.password");
    }

    public String getDbName(){
        return getProperty("db.name");
    }

    public String getDbPort(){
        return getProperty("db.port");
    }

    public String getDbSchema(){
        return getProperty("db.schema");
    }

    public String getLiquibaseChangeLog() {
        return getProperty("liquibase.changeLog");
    }

    public String getLiquibaseServiceSchema() {
        return getProperty("liquibase.serviceSchema");
    }
}
