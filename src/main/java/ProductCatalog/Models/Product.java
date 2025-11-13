package ProductCatalog.Models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Товар: ").append(name).append("\n")
                .append("Цена: ").append(String.format("%.2f", price)).append(" ₽");

        if (brand != null && !brand.isEmpty())
            sb.append("\nБренд: ").append(brand);
        if (category != null && !category.isEmpty())
            sb.append("\nКатегория: ").append(category);
        if (description != null && !description.isEmpty())
            sb.append("\nОписание: ").append(description);

        return sb.toString();
    }
}