package ProductCatalog.config;

import ProductCatalog.db.Config;
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
    public UserRepository userRepository(DataSource dataSource) {
        return new UserRepository(dataSource);
    }

    @Bean
    public ProductRepository productRepository(DataSource dataSource) {
        return new ProductRepository(dataSource);
    }

    @Bean
    public CatalogRepository catalogRepository(DataSource dataSource) {
        return new CatalogRepository(dataSource);
    }

    @Bean
    public AuditRepository auditRepository(DataSource dataSource) {
        return new AuditRepository(dataSource);
    }

    @Bean
    public AuditService auditService(AuditRepository auditRepository) {
        return new AuditService(auditRepository);
    }

    @Bean
    public UserService userService(UserRepository userRepository, AuditService auditService) {
        return new UserService(userRepository, auditService);
    }

    @Bean
    public CatalogService catalogService(CatalogRepository catalogRepository, AuditService auditService, UserService userService) {
        return new CatalogService(catalogRepository, auditService, userService);
    }

    @Bean
    public ProductService productService(ProductRepository productRepository, AuditService auditService, UserService userService) {
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
