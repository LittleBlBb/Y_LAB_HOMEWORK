package ProductCatalog.Config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {
    private static final Properties props = new Properties();

    static {
        try (FileInputStream input = new FileInputStream("config.properties")) {
            props.load(input);
        } catch (IOException e){
            System.err.println("Не удалось загрузить config.properties. " + e.getMessage());
        }
    }

    private static String getProperty(String key, String defaultValue){
        return props.getProperty(key, defaultValue);
    }

    public static String getDataFile() {
        return getProperty("data.file", "data.ser");
    }
}
