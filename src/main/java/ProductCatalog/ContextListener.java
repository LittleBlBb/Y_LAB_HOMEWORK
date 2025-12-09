package ProductCatalog;

import ProductCatalog.db.Config;
import ProductCatalog.db.DBConnection;
import ProductCatalog.db.Migrator;
import ProductCatalog.repositories.implemetations.AuditRepository;
import ProductCatalog.repositories.implemetations.CatalogRepository;
import ProductCatalog.repositories.implemetations.ProductRepository;
import ProductCatalog.repositories.implemetations.UserRepository;
import ProductCatalog.repositories.interfaces.IAuditRepository;
import ProductCatalog.repositories.interfaces.ICatalogRepository;
import ProductCatalog.repositories.interfaces.IProductRepository;
import ProductCatalog.repositories.interfaces.IUserRepository;
import ProductCatalog.services.implementations.AuditService;
import ProductCatalog.services.implementations.CatalogService;
import ProductCatalog.services.implementations.ProductService;
import ProductCatalog.services.implementations.UserService;
import ProductCatalog.services.interfaces.IAuditService;
import ProductCatalog.services.interfaces.ICatalogService;
import ProductCatalog.services.interfaces.IProductService;
import ProductCatalog.services.interfaces.IUserService;
import org.postgresql.ds.PGSimpleDataSource;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Config config = new Config();

        DBConnection db = new DBConnection(config);
        PGSimpleDataSource dataSource = db.getDataSource();

        try {
            Migrator migrator = new Migrator(config, db);
            migrator.migrate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to migrate", e);
        }

        IUserRepository userRepo = new UserRepository(dataSource);
        ICatalogRepository catalogRepo = new CatalogRepository(dataSource);
        IProductRepository productRepo = new ProductRepository(dataSource);
        IAuditRepository auditRepo = new AuditRepository(dataSource);

        IAuditService auditService = new AuditService(auditRepo);
        IUserService userService = new UserService(userRepo, auditService);
        ICatalogService catalogService = new CatalogService(catalogRepo, auditService, userService);
        IProductService productService = new ProductService(productRepo, auditService, userService);

        sce.getServletContext().setAttribute("userService", userService);
        sce.getServletContext().setAttribute("catalogService", catalogService);
        sce.getServletContext().setAttribute("productService", productService);
        sce.getServletContext().setAttribute("auditService", auditService);
    }
}
