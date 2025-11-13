package ProductCatalog.Models;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
public class AuditEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LocalDateTime timestamp;
    private final String username;
    private final String action;
    private final String details;

    public AuditEntry(String username, String action, String details) {
        this.timestamp = LocalDateTime.now();
        this.username = username;
        this.action = action;
        this.details = details;
    }
}
