package ProductCatalog.Services;

import ProductCatalog.Models.AuditEntry;
import ProductCatalog.UnitOfWork;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AuditService implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UnitOfWork unitOfWork;

    public AuditService(UnitOfWork unitOfWork){
        this.unitOfWork = unitOfWork;
    }

    public void logAction(String username, String action, String details){
        unitOfWork.getAuditLog().add(new AuditEntry(username, action, details));
    }

    public List<AuditEntry> getAuditLog() {
        return new ArrayList<>(unitOfWork.getAuditLog());
    }

    public void clearLog(){
        unitOfWork.getAuditLog().clear();
    }
}
