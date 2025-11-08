package ProductCatalog.Services;

import ProductCatalog.Models.Catalog;
import ProductCatalog.Models.Product;
import ProductCatalog.Models.User;
import ProductCatalog.UnitOfWork;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductCatalogService {
    private static ProductCatalogService instance;
    private final UnitOfWork unitOfWork;

    private final Map<Integer, List<Product>> productsCache = new HashMap<>();

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
        if (productsCache.containsKey(catalogIndex)){
            return productsCache.get(catalogIndex);
        }
        Catalog catalog = getCatalogByIndex(catalogIndex);
        List<Product> result = catalog != null ? catalog.getProducts() : new ArrayList<>();
        productsCache.put(catalogIndex, result);
        return catalog != null ? catalog.getProducts() : new ArrayList<>();
    }

    private void invalidateAllCaches() {
        productsCache.clear();
    }

    public boolean deleteProduct(Product product) {
        boolean success = unitOfWork.deleteProduct(product);
        if (success){
            invalidateAllCaches();
            String username = (UserService.getInstance().getCurrentUser() != null)
                    ? UserService.getInstance().getCurrentUser().getUsername()
                    : null;
            unitOfWork.getInstance().logAction(username, "DELETE_PRODUCT",
                    "Удален товар: id= " + product.getId() + ", name=" + product.getName());
        }
        return success;
    }

    public boolean updateProduct(Product oldProduct, Product newProduct) {
        boolean success = unitOfWork.updateProduct(oldProduct, newProduct);
        if (success) {
            invalidateAllCaches();
            String username = (UserService.getInstance().getCurrentUser() != null)
                    ? UserService.getInstance().getCurrentUser().getUsername()
                    : null;
            UnitOfWork.getInstance().logAction(username, "UPDATE_PRODUCT",
                    "Изменен товар: oldId=" + oldProduct.getId() + ", newName=" + newProduct.getName());
        }
        return success;
    }

    public boolean createCatalog(Catalog catalog) {
        boolean success = unitOfWork.createCatalog(catalog);
        if (success) invalidateAllCaches();
        return success;
    }

    public boolean createProduct(Product product, int catalogIndex){
        boolean success = unitOfWork.createProduct(product, catalogIndex);
        if (success){
            invalidateAllCaches();
            String username = (UserService.getInstance().getCurrentUser() != null)
                    ? UserService.getInstance().getCurrentUser().getUsername()
                    : null;
            UnitOfWork.getInstance().logAction(username, "CREATE_PRODUCT",
                    "Создан товар: id=" + product.getId() + ", name=" + product.getName() +
                    ", catalogIndex=" + catalogIndex);

        }
        return success;
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
