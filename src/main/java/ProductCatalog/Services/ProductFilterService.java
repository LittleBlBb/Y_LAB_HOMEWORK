package ProductCatalog.Services;

import ProductCatalog.Models.Product;

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
    public List<Product> filterProductsByCategory(List<Product> products, String category) {
        long start = System.currentTimeMillis();
        List<Product> filtered = new ArrayList<>();
        for (Product p : products) {
            if (p.getCategory() != null && p.getCategory().equalsIgnoreCase(category)) {
                filtered.add(p);
            }
        }
        MetricsService.getInstance().displayMetrics("Фильтрация по категории", start);
        return filtered;
    }

    /**
     * Фильтрует товары по части названия.
     *
     * @param products список всех товаров
     * @param name часть названия для поиска
     * @return список товаров, содержащих указанную строку в названии
     */
    public List<Product> filterProductsByName(List<Product> products, String name) {
        long start = System.currentTimeMillis();
        List<Product> filtered = new ArrayList<>();
        for (Product p : products) {
            if (p.getName().toLowerCase().contains(name.toLowerCase())) {
                filtered.add(p);
            }
        }
        MetricsService.getInstance().displayMetrics("Фильтрация по имени", start);
        return filtered;
    }

    /**
     * Фильтрует товары по диапазону цен.
     *
     * @param products список всех товаров
     * @param minPrice минимальная цена
     * @param maxPrice максимальная цена
     * @return список товаров в указанном ценовом диапазоне
     */
    public List<Product> filterProductsByPriceRange(List<Product> products, double minPrice, double maxPrice) {
        long start = System.currentTimeMillis();
        List<Product> filtered = new ArrayList<>();
        for (Product p : products) {
            if (p.getPrice() >= minPrice && p.getPrice() <= maxPrice) {
                filtered.add(p);
            }
        }
        MetricsService.getInstance().displayMetrics("Фильтрация по ценовому диапазону", start);
        return filtered;
    }

    /**
     * Фильтрует товары по бренду.
     *
     * @param products список всех товаров
     * @param brand бренд для фильтрации
     * @return список товаров указанного бренда
     */
    public List<Product> filterProductsByBrand(List<Product> products, String brand) {
        long start = System.currentTimeMillis();
        List<Product> filtered = new ArrayList<>();
        for (Product p : products) {
            if (p.getBrand() != null && p.getBrand().equalsIgnoreCase(brand)) {
                filtered.add(p);
            }
        }
        MetricsService.getInstance().displayMetrics("Фильтрация по бренду", start);
        return filtered;
    }
}
