package ProductCatalog.dto;

import java.util.Objects;

public class AuditEntryDTO {
    private long id;
    private String username;
    private String action;
    private String details;
    private String timestamp;

    public AuditEntryDTO() {}

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuditEntryDTO that)) return false;
        return id == that.id
                && Objects.equals(username, that.username)
                && Objects.equals(action, that.action)
                && Objects.equals(details, that.details)
                && Objects.equals(timestamp, that.timestamp);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, action, details, timestamp);
    }
}