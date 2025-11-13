package ProductCatalog.Services;

import ProductCatalog.Models.Catalog;
import ProductCatalog.UnitOfWork;

import java.util.List;

public class CatalogService {
    private final AuditService auditService;
    private final UnitOfWork unitOfWork;
    private final UserService userService;

    public CatalogService(UnitOfWork unitOfWork, AuditService auditService, UserService userService) {
        this.unitOfWork = unitOfWork;
        this.auditService = auditService;
        this.userService = userService;
    }

    public List<Catalog> getAllCatalogs(){
        return unitOfWork.getCatalogs();
    }

    public Catalog getCatalogByIndex(int index) {
        List<Catalog> catalogs = unitOfWork.getCatalogs();
        if (index < 0 || index >= catalogs.size()) return null;
        return catalogs.get(index);
    }

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

    public boolean removeCatalog(Catalog catalog){
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