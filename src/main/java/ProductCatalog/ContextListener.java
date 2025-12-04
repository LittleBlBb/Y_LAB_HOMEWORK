package ProductCatalog;

import ProductCatalog.db.DBConnection;
import ProductCatalog.repositories.implemetations.AuditRepository;
import ProductCatalog.repositories.implemetations.CatalogRepository;
import ProductCatalog.repositories.implemetations.ProductRepository;
import ProductCatalog.repositories.implemetations.UserRepository;
import ProductCatalog.repositories.interfaces.IAuditRepository;
import ProductCatalog.repositories.interfaces.ICatalogRepository;
import ProductCatalog.repositories.interfaces.IProductRepository;
import ProductCatalog.repositories.interfaces.IUserRepository;
import ProductCatalog.services.implemetations.AuditService;
import ProductCatalog.services.implemetations.CatalogService;
import ProductCatalog.services.implemetations.ProductService;
import ProductCatalog.services.implemetations.UserService;
import ProductCatalog.services.interfaces.IAuditService;
import ProductCatalog.services.interfaces.ICatalogService;
import ProductCatalog.services.interfaces.IProductService;
import ProductCatalog.services.interfaces.IUserService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import javax.sql.DataSource;

@WebListener
public class ContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DataSource dataSource = DBConnection.getDataSource();

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
