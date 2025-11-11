package ProductCatalog.Services;

import ProductCatalog.Models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductFilterService {
    private static ProductFilterService instance;

    private ProductFilterService(){}

    public static ProductFilterService getInstance(){
        if (instance == null){
            instance = new ProductFilterService();
        }
        return instance;
    }

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
