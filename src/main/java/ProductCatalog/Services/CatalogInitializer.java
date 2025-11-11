package ProductCatalog.Services;

import ProductCatalog.Models.Product;

import java.util.ArrayList;
import java.util.List;

public class CatalogInitializer {
    public static List<Product> getDefaultProducts(String name) {
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
}
