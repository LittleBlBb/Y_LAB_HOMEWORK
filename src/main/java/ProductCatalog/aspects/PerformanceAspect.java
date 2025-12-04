package ProductCatalog.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class PerformanceAspect {

    @Around("@annotation(ProductCatalog.annotations.Performance)")
    public Object measure(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;

            System.out.println(
                    "[PERFORMANCE] " +
                            joinPoint.getSignature().toShortString() +
                            " took " + duration + " ms"
            );
        }
    }
}
