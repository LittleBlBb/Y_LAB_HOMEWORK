package ProductCatalog.config;

import ProductCatalog.db.Config;
import ProductCatalog.db.DBConnection;
import ProductCatalog.audit.repositories.implementations.AuditRepository;
import ProductCatalog.repositories.implemetations.CatalogRepository;
import ProductCatalog.repositories.implemetations.ProductRepository;
import ProductCatalog.repositories.implemetations.UserRepository;
import ProductCatalog.repositories.interfaces.IAuditRepository;
import ProductCatalog.repositories.interfaces.ICatalogRepository;
import ProductCatalog.repositories.interfaces.IProductRepository;
import ProductCatalog.repositories.interfaces.IUserRepository;
import ProductCatalog.audit.services.implementations.AuditService;
import ProductCatalog.services.implementations.CatalogService;
import ProductCatalog.services.MetricsService;
import ProductCatalog.services.ProductFilterService;
import ProductCatalog.services.implementations.ProductService;
import ProductCatalog.services.implementations.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class RootConfig {

    @Bean
    public Config appConfig() {
        return new Config();
    }

    @Bean
    public DBConnection dbConnection(Config appConfig) {
        return new DBConnection(appConfig);
    }

    @Bean
    public DataSource dataSource(DBConnection dbConnection) {
        return dbConnection.getDataSource();
    }

    @Bean
    public IUserRepository userRepository(DataSource dataSource) {
        return new UserRepository(dataSource);
    }

    @Bean
    public IProductRepository productRepository(DataSource dataSource) {
        return new ProductRepository(dataSource);
    }

    @Bean
    public ICatalogRepository catalogRepository(DataSource dataSource) {
        return new CatalogRepository(dataSource);
    }

    @Bean
    public IAuditRepository auditRepository(DataSource dataSource) {
        return new AuditRepository(dataSource);
    }

    @Bean
    public AuditService auditService(IAuditRepository auditRepository) {
        return new AuditService(auditRepository);
    }

    @Bean
    public UserService userService(IUserRepository userRepository, AuditService auditService) {
        return new UserService(userRepository, auditService);
    }

    @Bean
    public CatalogService catalogService(ICatalogRepository catalogRepository, AuditService auditService, UserService userService) {
        return new CatalogService(catalogRepository, auditService, userService);
    }

    @Bean
    public ProductService productService(IProductRepository productRepository, AuditService auditService, UserService userService) {
        return new ProductService(productRepository, auditService, userService);
    }

    @Bean
    public MetricsService metricsService(CatalogService catalogService){
        return MetricsService.getInstance(catalogService);
    }

    @Bean
    public ProductFilterService productFilterService(){
        return  ProductFilterService.getInstance();
    }
}
