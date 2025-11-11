package ProductCatalog.Services;

import ProductCatalog.Models.Catalog;
import ProductCatalog.Models.Product;
import ProductCatalog.UnitOfWork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductService
{
    private static ProductService instance;
    private final UnitOfWork unitOfWork;
    private final Map<Integer, List<Product>> productsCache = new HashMap<>();

    private ProductService(){
        unitOfWork = UnitOfWork.getInstance();
    }

    public static ProductService getInstance(){
        if (instance == null){
            instance = new ProductService();
        }
        return instance;
    }

    public List<Product> getProductsByCatalog(int catalogIndex) {
        if (productsCache.containsKey(catalogIndex)){
            return productsCache.get(catalogIndex);
        }
        Catalog catalog = CatalogService.getInstance().getCatalogByIndex(catalogIndex);
        List<Product> result = catalog != null ? catalog.getProducts() : new ArrayList<>();
        productsCache.put(catalogIndex, result);
        return catalog != null ? catalog.getProducts() : new ArrayList<>();
    }

    private void invalidateAllCaches() {
        productsCache.clear();
    }

    public boolean deleteProduct(Product product) {
        long start = System.currentTimeMillis();
        boolean success = unitOfWork.deleteProduct(product);
        if (success){
            invalidateAllCaches();
            String username = (UserService.getInstance().getCurrentUser() != null)
                    ? UserService.getInstance().getCurrentUser().getUsername()
                    : null;
            unitOfWork.getInstance().logAction(username, "DELETE_PRODUCT",
                    "Удален товар: id= " + product.getId() + ", name=" + product.getName());
        }
        MetricsService.getInstance().displayMetrics("Удаление товара", start);
        return success;
    }

    public boolean updateProduct(Product oldProduct, Product newProduct) {
        long start = System.currentTimeMillis();
        boolean success = unitOfWork.updateProduct(oldProduct, newProduct);
        if (success) {
            invalidateAllCaches();
            String username = (UserService.getInstance().getCurrentUser() != null)
                    ? UserService.getInstance().getCurrentUser().getUsername()
                    : null;
            UnitOfWork.getInstance().logAction(username, "UPDATE_PRODUCT",
                    "Изменен товар: oldId=" + oldProduct.getId() + ", newName=" + newProduct.getName());
        }
        MetricsService.getInstance().displayMetrics("Изменение товара", start);
        return success;
    }

    public boolean createProduct(Product product, int catalogIndex){
        long start = System.currentTimeMillis();
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
        MetricsService.getInstance().displayMetrics("Добавление нового товара", start);
        return success;
    }
}
