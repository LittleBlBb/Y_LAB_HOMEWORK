package ProductCatalog.constants;

import java.util.Set;

public enum Role {
    USER(Set.of()),
    ADMIN(Set.of(
            Permission.CREATE_PRODUCT,
            Permission.EDIT_PRODUCT,
            Permission.DELETE_PRODUCT,
            Permission.DELETE_CATALOG,
            Permission.VIEW_AUDIT,
            Permission.MANAGE_USERS,
            Permission.CREATE_CATALOG,
            Permission.EDIT_CATALOG
    ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
}