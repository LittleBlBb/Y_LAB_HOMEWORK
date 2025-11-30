package ProductCatalog.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private final Properties properties = new Properties();

    public Config() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("application.properties file not found");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file", e);
        }
    }

    public void override(String oldProperty, String newProperty) {
        properties.put(oldProperty, newProperty);
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
