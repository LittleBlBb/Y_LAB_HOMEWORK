package ProductCatalog;

import ProductCatalog.Services.*;
import ProductCatalog.UI.ProductCatalogUI;

public class Main {
    public static void main(String[] args) {
        System.out.println("""
                ===========================================
                Добро пожаловать в систему каталога товаров!
                ===========================================
                """);

        PersistenceService persistence = PersistenceService.getInstance();
        UnitOfWork unitOfWork = persistence.getUnitOfWork();

        AuditService auditService = new AuditService(unitOfWork);
        UserService userService = new UserService(unitOfWork, auditService);
        CatalogService catalogService = new CatalogService(unitOfWork, auditService, userService);
        ProductService productService = new ProductService(unitOfWork, auditService, userService);

        ProductCatalogUI ui = new ProductCatalogUI(catalogService, productService, userService, auditService);
        ui.run();

        persistence.saveData();
        System.out.println("Программа завершена. Данные сохранены.");
    }
}
