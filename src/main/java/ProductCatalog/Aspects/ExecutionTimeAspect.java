package ProductCatalog.Aspects;

import ProductCatalog.Annotations.LogExecution;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class ExecutionTimeAspect {

    @Around("@annotation(logExecution)")
    public Object measure(ProceedingJoinPoint pjp, LogExecution logExecution) throws Throwable {

        long start = System.currentTimeMillis();

        Object result = pjp.proceed();

        long time = System.currentTimeMillis() - start;

        System.out.println("[PERF] " + pjp.getSignature() + " executed in " + time + " ms");

        return result;
    }
}
