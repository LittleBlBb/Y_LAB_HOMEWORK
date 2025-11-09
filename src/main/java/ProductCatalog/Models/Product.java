package ProductCatalog.Models;

import lombok.Data;

import java.io.Serializable;

@Data
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int nextId = 1;
    private final int id;
    private double price;
    private String name;
    private String description;
    private String brand;
    private String category;

    public Product(String name, double price, String description, String brand, String category) {
        this.id = nextId++;
        this.name = name;
        this.price = price;
        this.description = description;
        this.brand = brand;
        this.category = category;
    }

    public Product(String name, double price) {
        this.id = nextId++;
        this.name = name;
        this.price = price;
        this.description = "";
        this.brand = "";
        this.category = "";
    }

    public String toShortString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\t").append(price);
        if (brand != null && !brand.isEmpty()) sb.append("\tБренд: ").append(brand);
        return sb.toString();
    }
}
