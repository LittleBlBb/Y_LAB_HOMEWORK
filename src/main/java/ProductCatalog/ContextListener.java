package ProductCatalog;

import ProductCatalog.db.DBConnection;
import ProductCatalog.repositories.AuditRepository;
import ProductCatalog.repositories.CatalogRepository;
import ProductCatalog.repositories.ProductRepository;
import ProductCatalog.repositories.UserRepository;
import ProductCatalog.services.AuditService;
import ProductCatalog.services.CatalogService;
import ProductCatalog.services.MetricsService;
import ProductCatalog.services.ProductFilterService;
import ProductCatalog.services.ProductService;
import ProductCatalog.services.UserService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.postgresql.ds.PGSimpleDataSource;

@WebListener
public class ContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
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
        ProductFilterService.getInstance();

        sce.getServletContext().setAttribute("userService", userService);
        sce.getServletContext().setAttribute("catalogService", catalogService);
        sce.getServletContext().setAttribute("productService", productService);
        sce.getServletContext().setAttribute("auditService", auditService);
    }
}
