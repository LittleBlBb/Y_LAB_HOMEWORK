package ProductCatalog.Services;

import ProductCatalog.Models.Catalog;
import ProductCatalog.UnitOfWork;
import lombok.Getter;

import java.util.List;

public class MetricsService {
    @Getter
    private static MetricsService instance;
    private final CatalogService catalogService;

    private MetricsService(CatalogService catalogService, ProductService productService) {
        this.catalogService = catalogService;
    }

    public static MetricsService getInstance(CatalogService catalogService, ProductService productService) {
        if (instance == null) {
            instance = new MetricsService(catalogService, productService);
        }
        return instance;
    }

    public int getTotalProductCount() {
        List<Catalog> catalogs = catalogService.getAllCatalogs();
        return catalogs != null ? catalogs.size() : 0;
    }

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