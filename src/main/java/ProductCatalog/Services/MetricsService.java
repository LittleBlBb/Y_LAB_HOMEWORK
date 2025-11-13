package ProductCatalog.Services;

import ProductCatalog.Models.Catalog;
import lombok.Getter;

import java.util.List;

/**
 * Сервис для вычисления и отображения метрик каталога.
 * Реализует шаблон Singleton.
 */
public class MetricsService {
    @Getter
    private static MetricsService instance;
    private final CatalogService catalogService;

    /**
     * Приватный конструктор для инициализации метрик.
     *
     * @param catalogService сервис каталогов
     * @param productService сервис товаров
     */
    private MetricsService(CatalogService catalogService, ProductService productService) {
        this.catalogService = catalogService;
    }

    /**
     * Возвращает единственный экземпляр {@code MetricsService}.
     *
     * @param catalogService сервис каталогов
     * @param productService сервис товаров
     * @return экземпляр {@code MetricsService}
     */
    public static MetricsService getInstance(CatalogService catalogService, ProductService productService) {
        if (instance == null) {
            instance = new MetricsService(catalogService, productService);
        }
        return instance;
    }

    /**
     * Возвращает общее количество продуктов во всех каталогах.
     *
     * @return количество продуктов
     */
    public int getTotalProductCount() {
        List<Catalog> catalogs = catalogService.getAllCatalogs();
        return catalogs != null ? catalogs.size() : 0;
    }

    /**
     * Возвращает общее количество каталогов.
     *
     * @return количество каталогов
     */
    public int getTotalCatalogCount() {
        int count = 0;
        List<Catalog> catalogs = catalogService.getAllCatalogs();
        if (catalogs != null) {
            for (Catalog c : catalogs) {
                count += c.getProducts().size();
            }
        }
        return count;
    }

    /**
     * Отображает метрики в консоли.
     *
     * @param actionName название действия
     * @param startTime  время начала выполнения
     */
    public void displayMetrics(String actionName, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("""
                \n============ Метрики ============
                Действие: %s
                Время выполнения: %d мс
                Каталогов: %d
                Товаров всего: %d
                ===================================
                """.formatted(
                actionName,
                duration,
                getTotalCatalogCount(),
                getTotalProductCount()
        ));
    }
}
