package ProductCatalog.Models;

import java.time.LocalDateTime;

public class AuditEntry {
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getUsername() {
        return username;
    }

    public String getAction() {
        return action;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public String toString(){
        return "[" + timestamp + "] " + (username != null ? username :"ANONYMOUS") +
                " -> " + action + " : " + details;
    }
}
