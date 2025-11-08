package ProductCatalog.Services;

import ProductCatalog.Models.Catalog;
import ProductCatalog.Models.Product;
import ProductCatalog.UnitOfWork;
import java.util.ArrayList;
import java.util.List;

public class ProductCatalogService {
    private static ProductCatalogService instance;
    private final UnitOfWork unitOfWork;

    private ProductCatalogService() {
        this.unitOfWork = UnitOfWork.getInstance();
    }

    public static ProductCatalogService getInstance() {
        if (instance == null) {
            instance = new ProductCatalogService();
        }
        return instance;
    }

    public List<Catalog> getAllCatalogs() {
        return unitOfWork.getCatalogs();
    }

    public Catalog getCatalogByIndex(int index) {
        List<Catalog> catalogs = unitOfWork.getCatalogs();
        if (index < 0 || index >= catalogs.size()) return null;
        return catalogs.get(index);
    }

    public List<Product> getProductsByCatalog(int catalogIndex) {
        Catalog catalog = getCatalogByIndex(catalogIndex);
        return catalog != null ? catalog.getProducts() : new ArrayList<>();
    }

    public void deleteProduct(Product product) {
        unitOfWork.deleteProduct(product);
    }

    public void updateProduct(Product oldProduct, Product newProduct) {
        unitOfWork.updateProduct(oldProduct, newProduct);
    }

    public void createCatalog(Catalog catalog) {
        unitOfWork.createCatalog(catalog);
    }

    public void createProduct(Product product, int catalogIndex){
        unitOfWork.createProduct(product, catalogIndex);
    }

    public List<Product> filterProductsByCategory(int catalogIndex, String category) {
        List<Product> filtered = new ArrayList<>();
        List<Product> products = getProductsByCatalog(catalogIndex);
        for (Product p : products) {
            if (p.getCategory() != null && p.getCategory().equalsIgnoreCase(category)) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    public List<Product> filterProductsByName(int catalogIndex, String name) {
        List<Product> filtered = new ArrayList<>();
        List<Product> products = getProductsByCatalog(catalogIndex);
        for (Product p : products) {
            if (p.getName().toLowerCase().contains(name.toLowerCase())) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    public List<Product> filterProductsByPriceRange(int catalogIndex, double minPrice, double maxPrice) {
        List<Product> filtered = new ArrayList<>();
        List<Product> products = getProductsByCatalog(catalogIndex);
        for (Product p : products) {
            if (p.getPrice() >= minPrice && p.getPrice() <= maxPrice) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    public List<Product> filterProductsByBrand(int catalogIndex, String brand) {
        List<Product> filtered = new ArrayList<>();
        List<Product> products = getProductsByCatalog(catalogIndex);
        for (Product p : products) {
            if (p.getBrand() != null && p.getBrand().equalsIgnoreCase(brand)) {
                filtered.add(p);
            }
        }
        return filtered;
    }
}
