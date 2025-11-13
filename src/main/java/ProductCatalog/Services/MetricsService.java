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
     */
    private MetricsService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    /**
     * Возвращает единственный экземпляр {@code MetricsService}.
     *
     * @param catalogService сервис каталогов
     * @return экземпляр {@code MetricsService}
     */
    public static MetricsService getInstance(CatalogService catalogService) {
        if (instance == null) {
            instance = new MetricsService(catalogService);
        }
        return instance;
    }

    /**
     * Возвращает единственный экземпляр {@link MetricsService}.
     * <p>
     * Этот метод используется для получения уже инициализированного экземпляра
     * сервиса метрик (реализует паттерн Singleton).
     * Если экземпляр ещё не был создан через вызов {@code getInstance(CatalogService, ProductService)},
     * будет выброшено исключение {@link IllegalStateException}.
     *
     * @return экземпляр {@link MetricsService}
     * @throws IllegalStateException если сервис не был инициализирован ранее
     */
    public static MetricsService getInstance(){
        if (instance == null){
            throw new IllegalStateException("MetricsService not initialized.");
        }
        return instance;
    }

    /**
     * Возвращает общее количество продуктов во всех каталогах.
     *
     * @return количество продуктов
     */
    public int getTotalCatalogCount() {
        List<Catalog> catalogs = catalogService.getAllCatalogs();
        return catalogs != null ? catalogs.size() : 0;
    }

    /**
     * Возвращает общее количество каталогов.
     *
     * @return количество каталогов
     */
    public int getTotalProductCount() {
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
