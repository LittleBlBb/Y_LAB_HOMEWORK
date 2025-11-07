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

}
