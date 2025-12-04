package ProductCatalog.services;

import ProductCatalog.models.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для фильтрации товаров по различным критериям.
 * Реализует шаблон Singleton.
 */
public class ProductFilterService {
    private static ProductFilterService instance;

    /**
     * Приватный конструктор (реализация Singleton).
     */
    private ProductFilterService() {}

    /**
     * Возвращает единственный экземпляр {@code ProductFilterService}.
     *
     * @return экземпляр {@code ProductFilterService}
     */
    public static ProductFilterService getInstance() {
        if (instance == null) {
            instance = new ProductFilterService();
        }
        return instance;
    }

    /**
     * Фильтрует список товаров по категории.
     *
     * @param products список всех товаров
     * @param category категория для фильтрации
     * @return список товаров выбранной категории
     */
    public List<Product> filterByCategory(List<Product> products, String category) {
        long start = System.currentTimeMillis();
        List<Product> out = new ArrayList<>();
        for (Product p : products) {
            if (p.getCategory() != null &&
                    p.getCategory().equalsIgnoreCase(category)) {
                out.add(p);
            }
        }
        MetricsService.getInstance().displayMetrics("Фильтр по категории", start);
        return out;
    }

    /**
     * Фильтрует товары по части названия.
     *
     * @param products список всех товаров
     * @param name часть названия для поиска
     * @return список товаров, содержащих указанную строку в названии
     */
    public List<Product> filterByName(List<Product> products, String name) {
        long start = System.currentTimeMillis();
        List<Product> out = new ArrayList<>();

        String s = name.toLowerCase();
        for (Product p : products) {
            if (p.getName().toLowerCase().contains(s)) {
                out.add(p);
            }
        }
        MetricsService.getInstance().displayMetrics("Фильтр по имени", start);
        return out;
    }

    /**
     * Фильтрует товары по диапазону цен.
     *
     * @param products список всех товаров
     * @param min минимальная цена
     * @param max максимальная цена
     * @return список товаров в указанном ценовом диапазоне
     */
    public List<Product> filterByPrice(List<Product> products, double min, double max) {
        long start = System.currentTimeMillis();
        List<Product> out = new ArrayList<>();

        for (Product p : products) {
            if (p.getPrice() >= min && p.getPrice() <= max) {
                out.add(p);
            }
        }
        MetricsService.getInstance().displayMetrics("Фильтр по цене", start);
        return out;
    }

    /**
     * Фильтрует товары по бренду.
     *
     * @param products список всех товаров
     * @param brand бренд для фильтрации
     * @return список товаров указанного бренда
     */
    public List<Product> filterByBrand(List<Product> products, String brand) {
        long start = System.currentTimeMillis();
        List<Product> out = new ArrayList<>();

        for (Product p : products) {
            if (p.getBrand() != null &&
                    p.getBrand().equalsIgnoreCase(brand)) {
                out.add(p);
            }
        }
        MetricsService.getInstance().displayMetrics("Фильтр по бренду", start);
        return out;
    }
}
