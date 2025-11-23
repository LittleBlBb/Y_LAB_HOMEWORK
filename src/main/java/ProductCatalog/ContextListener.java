package ProductCatalog;

import ProductCatalog.Aspects.AuditAspect;
import ProductCatalog.DB.DBConnection;
import ProductCatalog.Repositories.AuditRepository;
import ProductCatalog.Repositories.CatalogRepository;
import ProductCatalog.Repositories.ProductRepository;
import ProductCatalog.Repositories.UserRepository;
import ProductCatalog.Services.AuditService;
import ProductCatalog.Services.CatalogService;
import ProductCatalog.Services.MetricsService;
import ProductCatalog.Services.ProductFilterService;
import ProductCatalog.Services.ProductService;
import ProductCatalog.Services.UserService;
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

        AuditAspect.setAuditService(auditService);

        sce.getServletContext().setAttribute("userService", userService);
        sce.getServletContext().setAttribute("catalogService", catalogService);
        sce.getServletContext().setAttribute("productService", productService);
        sce.getServletContext().setAttribute("auditService", auditService);
    }
}
