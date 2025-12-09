package ProductCatalog.repositories.interfaces;

import ProductCatalog.models.Product;

import java.util.List;

public interface IProductRepository {

    List<Product> findByCatalogId(long catalogId);
    Product save(Product p);
    boolean update(Product p);
    boolean delete(long id);
    List<Product> findAll();
    Product findById(long id);
}
