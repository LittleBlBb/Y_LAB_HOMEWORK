package ProductCatalog.Services;

import ProductCatalog.Models.Catalog;
import ProductCatalog.Models.Product;
import ProductCatalog.UnitOfWork;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogService {
    private static CatalogService instance;
    private final UnitOfWork unitOfWork;

    private final Map<Integer, List<Product>> productsCache = new HashMap<>();

    private CatalogService() {
        this.unitOfWork = UnitOfWork.getInstance();
    }

    public static CatalogService getInstance() {
        if (instance == null) {
            instance = new CatalogService();
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

    public boolean createCatalog(Catalog catalog) {
        return unitOfWork.createCatalog(catalog);
    }
}