package ProductCatalog;

import ProductCatalog.Models.Catalog;
import ProductCatalog.Models.Product;

import java.util.ArrayList;
import java.util.List;

public class UnitOfWork {
    private static UnitOfWork instance;
    private final List<Catalog> catalogs;

    private UnitOfWork() {
        catalogs = new ArrayList<>();
        initializeCatalogs();
    }

    public static UnitOfWork getInstance() {
        if (instance == null) {
            instance = new UnitOfWork();
        }
        return instance;
    }

    private void initializeCatalogs() {
        catalogs.add(new Catalog("Смартфоны"));
        catalogs.add(new Catalog("Комплектующие для ПК"));
        catalogs.add(new Catalog("Бытовая техника"));
        catalogs.add(new Catalog("Офис и мебель"));
    }

    // --- Управление каталогами ---
    public List<Catalog> getCatalogs() {
        return new ArrayList<>(catalogs); // возвращаем копию, чтобы избежать внешних изменений
    }

    public void addCatalog(Catalog catalog) {
        if (catalog != null) {
            catalogs.add(catalog);
        }
    }

    public boolean removeCatalog(Catalog catalog) {
        return catalogs.remove(catalog);
    }

    public Catalog findCatalogByName(String name) {
        return catalogs.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    // --- Управление товарами ---
    public boolean deleteProduct(Product product) {
        for (Catalog catalog : catalogs) {
            List<Product> products = catalog.getProducts();
            if (products.contains(product)) {
                products.remove(product);
                return true;
            }
        }
        return false;
    }

    public boolean updateProduct(Product oldProduct, Product newProduct) {
        for (Catalog catalog : catalogs) {
            List<Product> products = catalog.getProducts();
            int index = products.indexOf(oldProduct);
            if (index >= 0) {
                products.set(index, newProduct);
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Список каталогов:\n");
        for (Catalog c : catalogs) {
            sb.append("- ").append(c.getName())
                    .append(" (").append(c.getProducts().size()).append(" товаров)\n");
        }
        return sb.toString();
    }
}
