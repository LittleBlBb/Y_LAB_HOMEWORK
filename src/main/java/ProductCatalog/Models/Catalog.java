package ProductCatalog.Models;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class Catalog implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int nextId = 1;
    private final int id;
    private String name;
    private final List<Product> products;

    public Catalog(String name, List<Product> products) {
        this.id = nextId++;
        this.name = name;
        this.products = new ArrayList<>(products);
    }

    public Catalog(String name){
        this.id = nextId++;
        this.name = name;
        products = new ArrayList<>();
    }

    public void addProduct(Product product) {
        if (product != null) products.add(product);
    }

    public boolean removeProduct(Product product) {
        return products.remove(product);
    }

    public Product findProductById(int id) {
        return products.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }
}
