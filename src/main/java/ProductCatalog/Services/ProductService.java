package ProductCatalog.Services;

import ProductCatalog.Models.Catalog;
import ProductCatalog.Models.Product;
import ProductCatalog.Repositories.ProductRepository;

import java.util.List;

/**
 * Сервис для управления товарами.
 * Позволяет создавать, изменять, удалять и получать товары из каталогов.
 */
public class ProductService {
    private final ProductRepository productRepository;
    private final AuditService auditService;
    private final UserService userService;

    /**
     * Создает экземпляр {@code ProductService}.
     *
     * @param productRepository объект, управляющий товарами из БД
     * @param auditService сервис аудита
     * @param userService сервис пользователей
     */
    public ProductService(ProductRepository productRepository, AuditService auditService, UserService userService) {
        this.productRepository = productRepository;
        this.userService = userService;
        this.auditService = auditService;
    }

    /**
     *
     * @param catalogId id каталога, продукты которого ищем
     * @return
     */
    public List<Product> getProducts(long catalogId){
        return productRepository.findByCatalog(catalogId);
    }

    /**
     *
     * @param id id товара, который удаляем
     * @param name название товара
     * @return
     */
    public boolean deleteProduct(long id, String name) {
        long start = System.currentTimeMillis();
        boolean deleted = productRepository.delete(id);
        if (deleted) {
            auditService.log(userService.getCurrentUser().getUsername(),
                    "DELETE_PRODUCT", "Удалён товар: " + name);
            MetricsService.getInstance().displayMetrics("Удаление товара", start);
        }
        return deleted;
    }

    /**
     * Обновляет информацию о товаре.
     *
     * @param newProduct новый товар
     * @return {@code true}, если обновление успешно
     */
    public boolean updateProduct(Product newProduct) {
        long start = System.currentTimeMillis();
        boolean updated = productRepository.update(newProduct);
        if (updated){
            auditService.log(userService.getCurrentUser().getUsername(),
                    "UPDATE_PRODUCT", "Изменён товар: " + newProduct.getName());
            MetricsService.getInstance().displayMetrics("Изменение товара", start);
        }
        return updated;
    }

    /**
     * Создает новый товар в указанном каталоге.
     *
     * @param product товар для добавления
     * @return {@code true}, если товар успешно добавлен
     */
    public boolean createProduct(Product product) {
        long start = System.currentTimeMillis();
        productRepository.save(product);
        String username = userService.getCurrentUser() != null
                ? userService.getCurrentUser().getUsername()
                : "system";
        auditService.log(username, "CREATE_PRODUCT", "Добавлен товар: " + product.getName());
        MetricsService.getInstance().displayMetrics("Добавление товара", start);
        return true;
    }
}
