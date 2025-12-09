package ProductCatalog.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.yaml.snakeyaml.Yaml;

public class Config {
    private final Properties properties = new Properties();

    public Config() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.yml")) {
            if (input == null) {
                throw new RuntimeException("application.yml file not found");
            }
            Yaml yaml = new Yaml();
            @SuppressWarnings("unchecked")
            Map<String, Object> yamlMap = yaml.load(input);
            flattenMap(yamlMap, "", properties);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file", e);
        }
    }

    private void flattenMap(Map<String, Object> map, String prefix, Properties properties) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) value;
                flattenMap(nestedMap, key, properties);
            } else {
                properties.put(key, value.toString());
            }
        }
    }

    public void override(String oldProperty, String newProperty) {
        properties.put(oldProperty, newProperty);
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}