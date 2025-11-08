package ProductCatalog.Services;

import ProductCatalog.Models.Catalog;
import ProductCatalog.UnitOfWork;

public class MetricsService {
    private static MetricsService instance;
    private final UnitOfWork unitOfWork;

    private MetricsService(){
        this.unitOfWork = UnitOfWork.getInstance();
    }

    public static MetricsService getInstance(){
        if (instance == null){
            instance = new MetricsService();
        }
        return instance;
    }

    public int getTotalProductCount() {
        int count = 0;
        for (Catalog c : unitOfWork.getCatalogs()) {
            count += c.getProducts().size();
        }
        return count;
    }

    public int getTotalCatalogCount() {
        return unitOfWork.getCatalogs().size();
    }

    public void displayMetrics(String actionName, long startTime){
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("\n============= Метрики ==============");
        System.out.println("Действие: " + actionName);
        System.out.println("Время выполнения: " + duration + "мс");
        System.out.println("Каталогов: " + getTotalCatalogCount());
        System.out.println("Товаров всего: " + getTotalProductCount());
        System.out.println("======================================");
    }
}
