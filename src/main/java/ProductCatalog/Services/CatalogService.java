package ProductCatalog.Services;

import ProductCatalog.Models.Catalog;
import ProductCatalog.UnitOfWork;

import java.util.List;

/**
 * Сервис для управления каталогами товаров.
 * Позволяет создавать, получать и удалять каталоги,
 * а также фиксирует действия в журнале аудита.
 */
public class CatalogService {
    private final AuditService auditService;
    private final UnitOfWork unitOfWork;
    private final UserService userService;

    /**
     * Создает экземпляр {@code CatalogService}.
     *
     * @param unitOfWork  объект, управляющий данными приложения
     * @param auditService сервис аудита
     * @param userService  сервис пользователей
     */
    public CatalogService(UnitOfWork unitOfWork, AuditService auditService, UserService userService) {
        this.unitOfWork = unitOfWork;
        this.auditService = auditService;
        this.userService = userService;
    }

    /**
     * Возвращает список всех каталогов.
     *
     * @return список каталогов
     */
    public List<Catalog> getAllCatalogs() {
        return unitOfWork.getCatalogs();
    }

    /**
     * Возвращает каталог по индексу.
     *
     * @param index индекс каталога в списке
     * @return каталог или {@code null}, если индекс некорректен
     */
    public Catalog getCatalogByIndex(int index) {
        List<Catalog> catalogs = unitOfWork.getCatalogs();
        if (index < 0 || index >= catalogs.size()) return null;
        return catalogs.get(index);
    }

    /**
     * Создает новый каталог и записывает действие в журнал аудита.
     *
     * @param catalog объект каталога для добавления
     * @return {@code true}, если каталог успешно создан
     */
    public boolean createCatalog(Catalog catalog) {
        if (catalog == null) return false;
        unitOfWork.getCatalogs().add(catalog);
        auditService.logAction(
                userService.getCurrentUser() != null
                        ? userService.getCurrentUser().getUsername()
                        : "system",
                "CREATE_CATALOG",
                "Создан новый каталог: " + catalog.getName()
        );
        return true;
    }

    /**
     * Удаляет указанный каталог и записывает действие в журнал аудита.
     *
     * @param catalog каталог для удаления
     * @return {@code true}, если каталог успешно удалён
     */
    public boolean removeCatalog(Catalog catalog) {
        if (catalog == null) return false;
        boolean removed = unitOfWork.getCatalogs().remove(catalog);
        if (removed) {
            auditService.logAction(
                    userService.getCurrentUser() != null
                            ? userService.getCurrentUser().getUsername()
                            : "system",
                    "DELETE_CATALOG",
                    "Удалён каталог: " + catalog.getName()
            );
        }
        return removed;
    }
}
