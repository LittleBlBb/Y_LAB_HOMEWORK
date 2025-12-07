package ProductCatalog;

import ProductCatalog.db.Config;
import ProductCatalog.db.DBConnection;
import ProductCatalog.db.Migrator;
import ProductCatalog.performance.starter.EnablePerformanceLogging;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnablePerformanceLogging
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

        SpringApplication.run(Main.class, args);
    }
}
