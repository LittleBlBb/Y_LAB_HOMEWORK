package ProductCatalog.aspects;

import ProductCatalog.annotations.Auditable;
import ProductCatalog.constants.SessionAttributes;
import ProductCatalog.models.User;
import ProductCatalog.services.implementations.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class AuditInterceptor implements MethodInterceptor {

    private final AuditService auditService;

    public AuditInterceptor(AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Auditable ann = method.getAnnotation(Auditable.class);

        if (ann == null) {
            return invocation.proceed();
        }

        HttpServletRequest request = extractRequest(invocation.getArguments());
        String username = resolveUser(request);
        String action = ann.action();
        String details = request != null ?
                "URL=" + request.getRequestURI() + ", HTTP=" + request.getMethod() :
                "no request info";

        try {
            Object result = invocation.proceed();
            auditService.save(username, action, details + ", status=OK");
            return result;
        } catch (Throwable ex) {
            auditService.save(username, action, details + ", status=ERROR, message=" + ex.getMessage());
            throw ex;
        }
    }

    private HttpServletRequest extractRequest(Object[] args) {
        if (args == null) return null;
        for (Object arg : args) {
            if (arg instanceof HttpServletRequest req) {
                return req;
            }
        }
        return null;
    }

    private String resolveUser(HttpServletRequest request) {
        if (request == null) return "anonymous";
        HttpSession session = request.getSession(false);
        if (session == null) return "anonymous";
        Object u = session.getAttribute(SessionAttributes.USER);
        if (u instanceof User user) {
            return user.getUsername();
        }
        return "anonymous";
    }
}
