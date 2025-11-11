package ProductCatalog;

import ProductCatalog.Config.AppConfig;
import ProductCatalog.Models.AuditEntry;
import ProductCatalog.Models.Catalog;
import ProductCatalog.Models.Product;
import ProductCatalog.Models.User;
import ProductCatalog.Services.CatalogInitializer;

import java.io.Serializable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.List;

public class UnitOfWork implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DATA_FILE = AppConfig.getDataFile();
    private static UnitOfWork instance;
    private final List<Catalog> catalogs;
    private final List<User> users;
    private final List<AuditEntry> auditLog;

    private UnitOfWork() {
        catalogs = new ArrayList<>(){{
            add(new Catalog("Смартфоны", CatalogInitializer.getDefaultProducts("Смартфоны")));
            add(new Catalog("Комплектующие для ПК", CatalogInitializer.getDefaultProducts("Комплектующие для ПК")));
            add(new Catalog("Бытовая техника", CatalogInitializer.getDefaultProducts("Бытовая техника")));
            add(new Catalog("Офис и мебель", CatalogInitializer.getDefaultProducts("Офис и мебель")));
        }};
        users = new ArrayList<>(){{
            add(new User("admin", "admin", "admin"));
        }};
        auditLog = new ArrayList<>();
    }

    public static UnitOfWork getInstance() {
        if (instance == null) {
            instance = loadData();
            if (instance == null) {
                instance = new UnitOfWork();
            }
        }
        return instance;
    }

    // ======= Работа с пользователями =========
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

    // ====== Управление каталогами ==========
    public List<Catalog> getCatalogs() {
        return new ArrayList<>(catalogs);
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
        for (Catalog catalog : catalogs) {
            if (catalog.getName().equalsIgnoreCase(name)) {
                return catalog;
            }
        }
        return null;
    }

    // ====== Управление товарами ========
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

    // ===== Аудит =======
    public void logAction(String username, String action, String details){
        auditLog.add(new AuditEntry(username, action, details));
    }

    public List<AuditEntry> getAuditLog() {
        return new ArrayList<>(auditLog);
    }

    // ==== Сериализация ====
    public static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(instance);
            System.out.println("Данные успешно сохранены.");
        } catch (IOException e){
            System.err.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }

    private static UnitOfWork loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            UnitOfWork loaded = (UnitOfWork) ois.readObject();
            System.out.println("Данные успешно загружены из файла.");
            return loaded;
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке данных: " + e.getMessage());
            return null;
        }
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
