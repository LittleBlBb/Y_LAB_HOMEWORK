package ProductCatalog.Services;

import ProductCatalog.Models.Catalog;
import ProductCatalog.Models.Product;
import ProductCatalog.Models.User;
import ProductCatalog.UnitOfWork;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductService
{
    private final UnitOfWork unitOfWork;
    private final AuditService auditService;
    private final UserService userService;
    private final Map<Integer, List<Product>> productsCache = new HashMap<>();

    public ProductService(UnitOfWork unitOfWork, AuditService auditService, UserService userService){
        this.unitOfWork = unitOfWork;
        this.userService = userService;
        this.auditService = auditService;
    }

    public List<Product> getProductsByCatalog(int catalogIndex) {
        if (productsCache.containsKey(catalogIndex)){
            return productsCache.get(catalogIndex);
        }
        List<Catalog> catalogs = unitOfWork.getCatalogs();
        if (catalogIndex < 0 || catalogIndex >= catalogs.size()) return Collections.emptyList();
        List<Product> result = catalogs.get(catalogIndex).getProducts();
        productsCache.put(catalogIndex, result);
        return result;
    }

    private void invalidateCache() {
        productsCache.clear();
    }

    public boolean deleteProduct(Product product) {
        long start = System.currentTimeMillis();
        for (Catalog catalog : unitOfWork.getCatalogs()){
            if (catalog.getProducts().remove(product)){
                invalidateCache();
                auditService.logAction(
                        userService.getCurrentUser() != null
                        ? userService.getCurrentUser().getUsername()
                                : "system",
                        "DELETE_PRODUCT",
                        "Удалён товар: " + product.getName()
                );
                MetricsService.getInstance().displayMetrics("Удаление товара", start);
                return true;
            }
        }
        return false;
    }

    public boolean updateProduct(Product oldProduct, Product newProduct) {
        long start = System.currentTimeMillis();

        for (Catalog catalog : unitOfWork.getCatalogs()){
            List<Product> products = catalog.getProducts();
            int index = products.indexOf(oldProduct);
            if (index >= 0){
                products.set(index, newProduct);
                invalidateCache();
                auditService.logAction(
                        userService.getCurrentUser() != null
                        ? userService.getCurrentUser().getUsername()
                                : "system",
                        "UPDATE_PRODUCT",
                        "Изменён товар: " + oldProduct.getName() + " -> " + newProduct.getName()
                );
                MetricsService.getInstance().displayMetrics("Изменение товара", start);
                return true;
            }
        }
        return false;
    }

    public boolean createProduct(Product product, int catalogIndex){
        long start = System.currentTimeMillis();
        List<Catalog> catalogs = unitOfWork.getCatalogs();
        if (product == null || catalogIndex < 0 || catalogIndex >= catalogs.size()) return false;
        catalogs.get(catalogIndex).getProducts().add(product);
        invalidateCache();

        auditService.logAction(
                userService.getCurrentUser() != null
                ? userService.getCurrentUser().getUsername()
                        : "system",
                "CREATE_PRODUCT",
                "Добавлен товар: " + product.getName()
        );
        MetricsService.getInstance().displayMetrics("Добавление товара", start);
        return true;
    }
}
