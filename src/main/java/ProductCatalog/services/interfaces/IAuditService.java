package ProductCatalog.services.interfaces;

import ProductCatalog.models.AuditEntry;

import java.util.List;

public interface IAuditService {
    void save(String username, String action, String details);
    List<AuditEntry> getAll();
}
