package ProductCatalog.repositories.interfaces;

import ProductCatalog.models.AuditEntry;

import java.util.List;

public interface IAuditRepository {
    AuditEntry save(AuditEntry entry);
    AuditEntry findById(long id);
    List<AuditEntry> findAll();
}
