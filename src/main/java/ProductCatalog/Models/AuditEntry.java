package ProductCatalog.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuditEntry {
    private long id;
    private String username;
    private String action;
    private String details;
    private LocalDateTime timestamp;


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