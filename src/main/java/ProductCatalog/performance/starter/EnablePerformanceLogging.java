package ProductCatalog.performance.starter;

import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(PerformanceConfiguration.class)
public @interface EnablePerformanceLogging {
}
