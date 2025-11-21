package ProductCatalog.DTO;

import lombok.Data;

@Data
public class AuditEntryDTO {
    long id;
    private final String username;
    private final String action;
    private final String details;
    private String timestamp;
}