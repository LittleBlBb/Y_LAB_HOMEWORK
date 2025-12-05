package ProductCatalog.services.interfaces;

import ProductCatalog.models.Catalog;

import java.util.List;

public interface ICatalogService {
    Catalog findById(long id);
    List<Catalog> getAll();
    boolean deleteCatalog(long id);
}
