package ProductCatalog.DB;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private final Properties properties = new Properties();

    public Config() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IOException("application.properties file not found");
            }
            properties.load(input);
        } catch (IOException e) {
            System.out.println("Error loading config: " + e.getMessage());
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
