package ProductCatalog.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public Map<String, String> handleAccessDenied(AccessDeniedException ex) {
        return Map.of(
                "error", ex.getMessage(),
                "status", "403"
        );
    }
}