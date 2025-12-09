package ProductCatalog.DTO;

import java.util.Objects;

public class ProductDTO {
    private long id;
    private Long catalogId;
    private String name;
    private double price;
    private String description;
    private String brand;
    private String category;

    public ProductDTO() {}

    public ProductDTO(long id, Long catalogId, String name, double price, String description, String brand, String category) {
        this.id = id;
        this.catalogId = catalogId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.brand = brand;
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Long catalogId) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductDTO that)) return false;
        return id == that.id
                && Objects.equals(name, that.name)
                && Objects.equals(price, that.price)
                && Objects.equals(brand, that.brand)
                && Objects.equals(category, that.category)
                && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, brand, category, description);
    }
}