package ProductCatalog.aspects;

import ProductCatalog.annotations.Performance;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class PerformanceInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Performance ann = method.getAnnotation(Performance.class);
        if (ann == null) return invocation.proceed();

        long start = System.currentTimeMillis();
        try {
            return invocation.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            System.out.println(
                    "[PERFORMANCE] " +
                            method.getDeclaringClass().getSimpleName() + "." + method.getName() +
                            " took " + duration + " ms"
            );
        }
    }
}
