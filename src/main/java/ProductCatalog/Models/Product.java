package ProductCatalog.Models;

import lombok.Data;

@Data
public class Product {

    private long id;
    private long catalogId;
    private String name;
    private double price;
    private String description;
    private String brand;
    private String category;

    public Product(long id, long catalogId, String name, double price, String description, String brand, String category) {
        this.id = id;
        this.catalogId = catalogId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.brand = brand;
        this.category = category;
    }

    public Product(long catalogId, String name, double price, String description, String brand, String category) {
        this.catalogId = catalogId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.brand = brand;
        this.category = category;
    }

    public String toShortString() {
        return brand + " " + name + " - " + price + "$";
    }

    @Override
    public String toString() {
        return """
                Бренд: %s
                Название: %s
                Цена: %.2f
                Категория: %s
                Описание: %s
                """.formatted(
                        brand,
                name,
                price,
                category,
                description);
    }
}