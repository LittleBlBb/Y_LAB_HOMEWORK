package ProductCatalog.repositories.interfaces;

import ProductCatalog.models.Catalog;

import java.util.List;

public interface ICatalogRepository {
    List<Catalog> findAll();
    Catalog findById(long id);
    Catalog save(Catalog catalog);
    boolean delete(long id);
}