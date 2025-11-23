package ProductCatalog.Aspects;

import ProductCatalog.Annotations.Auditable;
import ProductCatalog.Services.AuditService;
import lombok.Setter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AuditAspect {
    @Setter
    private static AuditService auditService;

    @AfterReturning("@annotation(auditable)")
    public void audit(JoinPoint jp, Auditable auditable){
        String username = "system";
        String action = auditable.action();
        String details = "Called: " + jp.getSignature();

        auditService.log(username, action, details);
    }

}
