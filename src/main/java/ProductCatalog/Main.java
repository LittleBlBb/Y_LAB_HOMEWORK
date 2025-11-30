package ProductCatalog;

import ProductCatalog.db.Config;
import ProductCatalog.db.DBConnection;
import ProductCatalog.db.Migrator;
import ProductCatalog.Repositories.AuditRepository;
import ProductCatalog.Repositories.CatalogRepository;
import ProductCatalog.Repositories.ProductRepository;
import ProductCatalog.Repositories.UserRepository;
import ProductCatalog.Services.*;
import ProductCatalog.UI.ProductCatalogUI;
import org.postgresql.ds.PGSimpleDataSource;

public class Main {
    public static void main(String[] args) {
        Config config = new Config();

        DBConnection db = new DBConnection(config);
        PGSimpleDataSource dataSource = db.getDataSource();

        try {
            Migrator migrator = new Migrator(config, db);
            migrator.migrate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to migrate", e);
        }

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
