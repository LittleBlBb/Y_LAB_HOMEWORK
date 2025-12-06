package ProductCatalog.config;

import ProductCatalog.annotations.Auditable;
import ProductCatalog.annotations.Performance;
import ProductCatalog.aspects.AuditInterceptor;
import ProductCatalog.aspects.PerformanceInterceptor;
import ProductCatalog.services.implementations.AuditService;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AopConfig {

    @Bean
    public AuditInterceptor auditInterceptor(AuditService auditService) {
        return new AuditInterceptor(auditService);
    }

    @Bean
    public PerformanceInterceptor performanceInterceptor() {
        return new PerformanceInterceptor();
    }

    @Bean
    public Advisor auditAdvisor(AuditInterceptor auditInterceptor) {
        AnnotationMatchingPointcut pointcut =
                new AnnotationMatchingPointcut(null, Auditable.class, true);
        return new DefaultPointcutAdvisor(pointcut, auditInterceptor);
    }

    @Bean
    public Advisor performanceAdvisor(PerformanceInterceptor performanceInterceptor) {
        AnnotationMatchingPointcut pointcut =
                new AnnotationMatchingPointcut(null, Performance.class, true);
        return new DefaultPointcutAdvisor(pointcut, performanceInterceptor);
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator proxyCreator() {
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true); // используем CGLIB для классов
        return proxyCreator;
    }
}
