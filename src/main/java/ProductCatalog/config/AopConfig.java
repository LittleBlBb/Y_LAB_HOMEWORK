package ProductCatalog.config;

import ProductCatalog.annotations.Auditable;
import ProductCatalog.aspects.AuditInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AopConfig {

    @Bean
    public Advisor auditAdvisor(AuditInterceptor auditInterceptor) {
        AnnotationMatchingPointcut pointcut =
                new AnnotationMatchingPointcut(null, Auditable.class, true);
        return new DefaultPointcutAdvisor(pointcut, auditInterceptor);
    }
}