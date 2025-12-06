package ProductCatalog.services.implementations;

import ProductCatalog.models.Product;
import ProductCatalog.repositories.interfaces.IProductRepository;
import ProductCatalog.services.MetricsService;
import ProductCatalog.services.interfaces.IAuditService;
import ProductCatalog.services.interfaces.IProductService;
import ProductCatalog.services.interfaces.IUserService;

import java.util.List;

/**
 * Сервис для управления товарами.
 * Позволяет создавать, изменять, удалять и получать товары из каталогов.
 */
public class ProductService implements IProductService {
    private final IProductRepository productRepository;
    private final IAuditService auditService;
    private final IUserService userService;

    /**
     * Создает экземпляр {@code ProductService}.
     *
     * @param productRepository объект, управляющий товарами из БД
     * @param auditService сервис аудита
     * @param userService сервис пользователей
     */
    public ProductService(IProductRepository productRepository, IAuditService auditService, IUserService userService) {
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
        return productRepository.findByCatalogId(catalogId);
    }


    /**
     * Возвращает все товары из БД
     * @return
     */
    public List<Product> getAll(){
        return productRepository.findAll();
    }

    /**
     *
     * @param id id товара, который удаляем
     * @return
     */
    public boolean deleteProduct(long id) {
        Product product = productRepository.findById(id);
        if (product == null){
            return false;
        }

        long start = System.currentTimeMillis();
        boolean deleted = productRepository.delete(id);
        if (deleted) {
            auditService.save(userService.getCurrentUser() != null
                            ? userService.getCurrentUser().getUsername()
                            : "anonymous",
                    "DELETE_PRODUCT", "Удалён товар: " + product.getName());
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
            auditService.save(userService.getCurrentUser() != null
                    ? userService.getCurrentUser().getUsername()
                    : "anonymous",
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
                : "anonymous";
        auditService.save(username, "CREATE_PRODUCT", "Добавлен товар: " + product.getName());
        MetricsService.getInstance().displayMetrics("Добавление товара", start);
        return true;
    }

    public Product findById(long id) {
        return productRepository.findById(id);
    }
}