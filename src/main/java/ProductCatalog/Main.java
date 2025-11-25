package ProductCatalog;

import ProductCatalog.DB.DBConnection;
import ProductCatalog.Repositories.AuditRepository;
import ProductCatalog.Repositories.CatalogRepository;
import ProductCatalog.Repositories.ProductRepository;
import ProductCatalog.Repositories.UserRepository;
import ProductCatalog.Services.*;
import ProductCatalog.UI.ProductCatalogUI;
import org.postgresql.ds.PGSimpleDataSource;

public class Main {
    public static void main(String[] args) {

        PGSimpleDataSource dataSource = DBConnection.getDataSource();

        UserRepository userRepo = new UserRepository(dataSource);
        CatalogRepository catalogRepo = new CatalogRepository(dataSource);
        ProductRepository productRepo = new ProductRepository(dataSource);
        AuditRepository auditRepo = new AuditRepository(dataSource);


        AuditService auditService = new AuditService(auditRepo);
        UserService userService = new UserService(userRepo, auditService);
        CatalogService catalogService = new CatalogService(catalogRepo, auditService, userService);
        MetricsService.getInstance(catalogService);
        ProductService productService = new ProductService(productRepo, auditService, userService);

        ProductCatalogUI ui = new ProductCatalogUI(catalogService, productService, userService, auditService);
        ui.run();
    }
}
