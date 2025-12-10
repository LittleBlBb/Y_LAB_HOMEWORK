package ProductCatalog.services.interfaces;

import ProductCatalog.models.Product;

import java.util.List;

public interface IProductService {
    Product createProduct(Product product);
    boolean updateProduct(Product product);
    boolean deleteProduct(long id);
    Product findById(long id);
    List<Product> getAll();
    List<Product> getProducts(long catalogId);
}
