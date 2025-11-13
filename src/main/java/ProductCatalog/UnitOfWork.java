package ProductCatalog;

import ProductCatalog.Models.AuditEntry;
import ProductCatalog.Models.Catalog;
import ProductCatalog.Models.User;
import ProductCatalog.Services.CatalogService;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UnitOfWork implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Catalog> catalogs;
    private final List<User> users;
    private final List<AuditEntry> auditLog;

    public UnitOfWork() {
        this.catalogs = new ArrayList<>();
        this.users = new ArrayList<>();
        this.auditLog = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Список каталогов:\n");
        for (Catalog c : catalogs) {
            sb.append("- ").append(c.getName())
                    .append(" (").append(c.getProducts().size()).append(" товаров)\n");
        }
        return sb.toString();
    }
}
