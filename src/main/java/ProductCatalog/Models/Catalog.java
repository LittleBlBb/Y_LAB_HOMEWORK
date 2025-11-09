package ProductCatalog.Models;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class Catalog implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int nextId = 1;
    private final int id;
    private String name;
    private final List<Product> products;

    public Catalog(String name) {
        this.id = nextId++;
        this.name = name;
        this.products = initializeProducts(name);
    }

    private static List<Product> initializeProducts(String name) {
        List<Product> list = new ArrayList<>();
        switch (name) {
            case "Смартфоны" -> {
                list.add(new Product("Смартфон Apple iPhone 15", 999.99));
                list.add(new Product("Смартфон Samsung Galaxy S24", 899.00));
                list.add(new Product("Планшет Apple iPad Air 10", 500.00));
            }
            case "Комплектующие для ПК" -> {
                list.add(new Product("Видеокарта RTX 5090 Ti", 1500.00));
                list.add(new Product("Видеокарта GTX 1660 Super", 130.00));
                list.add(new Product("Процессор Intel Core i9-9900K", 500.00));
            }
            case "Бытовая техника" -> {
                list.add(new Product("Индукционная варочная поверхность DEXP EH-I4MB/B", 100.00));
                list.add(new Product("Газовая варочная поверхность DEXP LD4GTB", 130.00));
                list.add(new Product("Скребок OneTwo O2SR019", 1.50));
            }
            case "Офис и мебель" -> {
                list.add(new Product("МФУ лазерное Kyocera ECOSYS M2040dn", 820.94));
                list.add(new Product("3D-сканер Creality CR-Scan Raptor", 1799.99));
                list.add(new Product("Компьютерное кресло ARDOR GAMING Chaos Guard 400M серый", 140.00));
            }
        }
        return list;
    }

    public void addProduct(Product product) {
        if (product != null) products.add(product);
    }

    public boolean removeProduct(Product product) {
        return products.remove(product);
    }

    public Product findProductById(int id) {
        return products.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }
}
