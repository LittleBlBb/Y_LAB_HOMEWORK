package ProductCatalog.audit.aspects;

import ProductCatalog.audit.annotations.Auditable;
import ProductCatalog.audit.services.implementations.AuditService;
import ProductCatalog.constants.SessionAttributes;
import ProductCatalog.models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuditAspect {

    private final AuditService auditService;

    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint pjp, Auditable auditable) throws Throwable {

        HttpServletRequest request = resolveRequest(pjp);

        String username = resolveUser(request);
        String action = auditable.action();

        String details = request != null
                ? "URL=" + request.getRequestURI() + ", HTTP=" + request.getMethod()
                : "no request info";

        try {
            Object result = pjp.proceed();
            auditService.save(username, action, details + ", status=OK");
            return result;

        } catch (Throwable ex) {
            auditService.save(username, action,
                    details + ", status=ERROR, message=" + ex.getMessage());
            throw ex;
        }
    }

    private HttpServletRequest resolveRequest(ProceedingJoinPoint pjp) {

        for (Object arg : pjp.getArgs()) {
            if (arg instanceof HttpServletRequest req) {
                return req;
            }
        }

        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        return attrs != null ? attrs.getRequest() : null;
    }

    private String resolveUser(HttpServletRequest request) {
        if (request == null) return "anonymous";

        HttpSession session = request.getSession(false);
        if (session == null) return "anonymous";

        Object user = session.getAttribute(SessionAttributes.USER);

        return (user instanceof User u) ? u.getUsername() : "anonymous";
    }
}
