package ProductCatalog.services;

import ProductCatalog.models.Catalog;
import ProductCatalog.repositories.CatalogRepository;

import java.util.List;

/**
 * Сервис для управления каталогами товаров.
 * Позволяет создавать, получать и удалять каталоги,
 * а также фиксирует действия в журнале аудита.
 */
public class CatalogService {
    private final AuditService auditService;
    private final CatalogRepository catalogRepository;
    private final UserService userService;

    /**
     * Создает экземпляр {@code CatalogService}.
     *
     * @param catalogRepository  объект, управляющий каталогами из БД
     * @param auditService сервис аудита
     * @param userService  сервис пользователей
     */
    public CatalogService(CatalogRepository catalogRepository, AuditService auditService, UserService userService) {
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
        auditService.save(
                userService.getCurrentUser() != null
                        ? userService.getCurrentUser().getUsername()
                        : "anonymous",
                "CREATE_CATALOG",
                "Создан новый каталог: " + catalog.getName()
        );
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

        if (deleted) {
            auditService.save(
                    userService.getCurrentUser() != null
                            ? userService.getCurrentUser().getUsername()
                            : "anonymous",
                    "DELETE_CATALOG",
                    "Удалён каталог: " + catalog.getName()
            );
        }
        return deleted;
    }

    public Catalog findById(long id) {
        return catalogRepository.findById(id);
    }
}
