package ProductCatalog.Models;

public class Product {

    private long id;
    private long catalogId;
    private String name;
    private double price;
    private String brand;
    private String category;
    private String description;

    public Product(long catalogId, String name, double price, String brand, String category, String description) {
        this.catalogId = catalogId;
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.category = category;
        this.description = description;
    }

    public Product(long id, long catalogId, String name, double price, String brand, String category, String description) {
        this.id = id;
        this.catalogId = catalogId;
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.category = category;
        this.description = description;
    }

    public Product() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(long catalogId) {
        this.catalogId = catalogId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
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