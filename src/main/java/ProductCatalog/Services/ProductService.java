package ProductCatalog.Services;

import ProductCatalog.Models.Catalog;
import ProductCatalog.Models.Product;
import ProductCatalog.UnitOfWork;

import java.util.*;

/**
 * Сервис для управления товарами.
 * Позволяет создавать, изменять, удалять и получать товары из каталогов.
 * Использует кеш для ускорения повторных запросов.
 */
public class ProductService {
    private final UnitOfWork unitOfWork;
    private final AuditService auditService;
    private final UserService userService;
    private final Map<Integer, List<Product>> productsCache = new HashMap<>();

    /**
     * Создает экземпляр {@code ProductService}.
     *
     * @param unitOfWork объект, управляющий данными приложения
     * @param auditService сервис аудита
     * @param userService сервис пользователей
     */
    public ProductService(UnitOfWork unitOfWork, AuditService auditService, UserService userService) {
        this.unitOfWork = unitOfWork;
        this.userService = userService;
        this.auditService = auditService;
    }

    /**
     * Возвращает список товаров для указанного каталога.
     * Результаты кешируются для оптимизации производительности.
     *
     * @param catalogIndex индекс каталога
     * @return список товаров каталога
     */
    public List<Product> getProductsByCatalog(int catalogIndex) {
        if (productsCache.containsKey(catalogIndex)) {
            return productsCache.get(catalogIndex);
        }
        List<Catalog> catalogs = unitOfWork.getCatalogs();
        if (catalogIndex < 0 || catalogIndex >= catalogs.size()) return Collections.emptyList();
        List<Product> result = catalogs.get(catalogIndex).getProducts();
        productsCache.put(catalogIndex, result);
        return result;
    }

    /**
     * Очищает кеш товаров.
     */
    private void invalidateCache() {
        productsCache.clear();
    }

    /**
     * Удаляет указанный товар.
     * Записывает действие в журнал аудита.
     *
     * @param product товар для удаления
     * @return {@code true}, если товар успешно удалён
     */
    public boolean deleteProduct(Product product) {
        long start = System.currentTimeMillis();
        for (Catalog catalog : unitOfWork.getCatalogs()) {
            if (catalog.getProducts().remove(product)) {
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

    /**
     * Обновляет информацию о товаре.
     *
     * @param oldProduct старый товар
     * @param newProduct новый товар
     * @return {@code true}, если обновление успешно
     */
    public boolean updateProduct(Product oldProduct, Product newProduct) {
        long start = System.currentTimeMillis();

        for (Catalog catalog : unitOfWork.getCatalogs()) {
            List<Product> products = catalog.getProducts();
            int index = products.indexOf(oldProduct);
            if (index >= 0) {
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

    /**
     * Создает новый товар в указанном каталоге.
     *
     * @param product товар для добавления
     * @param catalogIndex индекс каталога
     * @return {@code true}, если товар успешно добавлен
     */
    public boolean createProduct(Product product, int catalogIndex) {
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
