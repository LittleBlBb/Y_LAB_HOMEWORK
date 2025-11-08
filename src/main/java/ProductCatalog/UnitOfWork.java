package ProductCatalog;

import ProductCatalog.Models.Catalog;
import ProductCatalog.Models.Product;
import ProductCatalog.Models.User;
import java.util.ArrayList;
import java.util.List;

public class UnitOfWork {
    private static UnitOfWork instance;
    private final List<Catalog> catalogs;
    private final List<User> users;
    private UnitOfWork() {
        catalogs = new ArrayList<>();
        users = new ArrayList<>();
        initializeCatalogs();
        initializeUsers();
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

    private void initializeUsers(){
        users.add(new User("admin", "admin", "admin"));
    }

    public void addUser(User user){
        if (user != null) users.add(user);
    }

    public User findUserByUsername(String username){
        for (User u : users){
            if (u.getUsername().equalsIgnoreCase(username)){
                return u;
            }
        }
        return null;
    }

    public List<User> getUsers(){
        return new ArrayList<>(users);
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

    public boolean createProduct(Product product, int catalogIndex) {
        if (product == null || catalogIndex < 0 || catalogIndex >= catalogs.size()) return false;
        Catalog catalog = catalogs.get(catalogIndex);
        catalog.getProducts().add(product);
        return true;
    }

    public boolean createCatalog(Catalog catalog) {
        if (catalog == null) return false;
        catalogs.add(catalog);
        return true;
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
