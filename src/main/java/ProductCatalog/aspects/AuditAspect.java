package ProductCatalog.aspects;

import ProductCatalog.annotations.Auditable;
import ProductCatalog.constants.SessionAttributes;
import ProductCatalog.models.User;
import ProductCatalog.services.implementations.AuditService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Aspect
public class AuditAspect {

    @Around("@annotation(ProductCatalog.annotations.Auditable)")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request = extractRequest(joinPoint);
        if (request == null) {
            return joinPoint.proceed();
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Auditable ann = signature.getMethod().getAnnotation(Auditable.class);

        String action = ann.action();
        String username = resolveUser(request);

        String details = "URL=" + request.getRequestURI() +
                ", HTTP=" + request.getMethod();

        AuditService auditService =
                (AuditService) request.getServletContext().getAttribute("auditService");

        try {
            Object result = joinPoint.proceed();

            if (auditService != null) {
                auditService.save(username, action, details + ", status=OK");
            }

            return result;

        } catch (Throwable ex) {
            if (auditService != null) {
                auditService.save(username, action,
                        details + ", status=ERROR, message=" + ex.getMessage());
            }
            throw ex;
        }
    }

    private HttpServletRequest extractRequest(ProceedingJoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof HttpServletRequest req) {
                return req;
            }
        }
        return null;
    }

    private String resolveUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "anonymous";
        }
        Object u = session.getAttribute(SessionAttributes.USER);
        if (u == null) return "anonymous";

        return ((User) u).getUsername();
    }
}
