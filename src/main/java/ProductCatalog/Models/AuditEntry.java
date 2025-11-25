package ProductCatalog.Models;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class AuditEntry {
    long id;
    private final String username;
    private final String action;
    private final String details;
    private LocalDateTime timestamp;

    public AuditEntry(long id, String username, String action, String details, LocalDateTime timestamp) {
        this.id = id;
        this.username = username;
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
    }

    public AuditEntry(String username, String action, String details, LocalDateTime timestamp) {
        this.username = username;
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
    }

    public AuditEntry(String username, String action, String details) {
        this.username = username;
        this.action = action;
        this.details = details;
    }

    @Override
    public String toString(){
        return timestamp + " | " + username + " | " + action + " | " + details;
    }
}