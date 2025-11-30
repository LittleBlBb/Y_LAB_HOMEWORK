package ProductCatalog.aspects;

import ProductCatalog.services.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AuditAspect {

    @Around("@annotation(ProductCatalog.Annotations.Auditable)")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = null;

        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof HttpServletRequest req) {
                request = req;
                break;
            }
        }
        if (request == null) {
            return joinPoint.proceed();
        }
        AuditService auditService = (AuditService) request.getServletContext().getAttribute("auditService");

        String username = "anonymous";

        var seesion = request.getSession(false);

        if (seesion != null) {
            Object u = seesion.getAttribute("username");
            if (u != null) {
                username = u.toString();
            }
        }

        String action = joinPoint.getSignature().getName();

        String details =
                "URL: " + request.getRequestURI() +
                ", HTTP: " + request.getMethod();

        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;

        details += ", executionTime=" + duration + "ms";

        if (auditService != null) {
            auditService.save(username, action, details);
        }

        return result;
    }
}
