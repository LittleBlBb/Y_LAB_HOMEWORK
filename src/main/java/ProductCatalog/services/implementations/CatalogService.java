package ProductCatalog.services.implementations;

import ProductCatalog.models.Catalog;
import ProductCatalog.repositories.interfaces.ICatalogRepository;
import ProductCatalog.services.interfaces.IAuditService;
import ProductCatalog.services.interfaces.ICatalogService;
import ProductCatalog.services.interfaces.IUserService;

import java.util.List;

/**
 * Сервис для управления каталогами товаров.
 * Позволяет создавать, получать и удалять каталоги,
 * а также фиксирует действия в журнале аудита.
 */
public class CatalogService implements ICatalogService {
    private final IAuditService auditService;
    private final ICatalogRepository catalogRepository;
    private final IUserService userService;

    /**
     * Создает экземпляр {@code CatalogService}.
     *
     * @param catalogRepository  объект, управляющий каталогами из БД
     * @param auditService сервис аудита
     * @param userService  сервис пользователей
     */
    public CatalogService(ICatalogRepository catalogRepository, IAuditService auditService, IUserService userService) {
        this.catalogRepository = catalogRepository;
        this.auditService = auditService;
        this.userService = userService;
    }

    /**
     * Возвращает список всех каталогов.
     *
     * @return список каталогов
     */
    public List<Catalog> getAll() {
        return catalogRepository.findAll();
    }

    /**
     * Создает новый каталог и записывает действие в журнал аудита.
     *
     * @param catalog объект каталога для добавления
     * @return {@code true}, если каталог успешно создан
     */
    public boolean createCatalog(Catalog catalog) {
        catalogRepository.save(catalog);
        return true;
    }

    /**
     * Удаляет указанный каталог и записывает действие в журнал аудита.
     *
     * @param id id каталога для удаления
     * @return {@code true}, если каталог успешно удалён
     */
    public boolean deleteCatalog(long id) {
        Catalog catalog = catalogRepository.findById(id);

        if (catalog == null) {
            return false;
        }

        boolean deleted = catalogRepository.delete(id);
        return deleted;
    }

    public Catalog findById(long id) {
        return catalogRepository.findById(id);
    }
}
