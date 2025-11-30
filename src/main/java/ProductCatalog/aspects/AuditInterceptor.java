package ProductCatalog.aspects;

import ProductCatalog.services.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuditInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        if (request == null) {
            return invocation.proceed();
        }

        AuditService auditService = (AuditService) request.getServletContext().getAttribute("auditService");

        String username = "anonymous";

        HttpSession session = request.getSession(false);
        if (session != null) {
            Object u = session.getAttribute("username");
            if (u != null) {
                username = u.toString();
            }
        }

        String action = invocation.getMethod().getName();

        String details = "URL: " + request.getRequestURI() +
                ", HTTP: " + request.getMethod();

        long start = System.currentTimeMillis();
        Object result = invocation.proceed();
        long duration = System.currentTimeMillis() - start;

        details += ", executionTime=" + duration + "ms";

        if (auditService != null) {
            auditService.save(username, action, details);
        }

        return result;
    }
}