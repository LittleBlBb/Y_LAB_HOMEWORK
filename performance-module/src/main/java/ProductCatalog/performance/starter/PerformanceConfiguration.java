package ProductCatalog.performance.starter;

import ProductCatalog.performance.aspects.PerformanceInterceptor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PerformanceConfiguration {

    @Bean
    public PerformanceInterceptor performanceInterceptor() {
        return new PerformanceInterceptor();
    }

    @Bean(name = "performanceAdvisor")
    public DefaultPointcutAdvisor performanceAdvisor() {
        return new DefaultPointcutAdvisor(
                new AnnotationMatchingPointcut(null, ProductCatalog.performance.annotations.Performance.class),
                performanceInterceptor()
        );
    }

    @Bean(name = "performanceDefaultAdvisorAutoProxyCreator")
    public DefaultAdvisorAutoProxyCreator performanceDefaultAdvisorAutoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }
}
