package ProductCatalog.Models;

import java.io.Serializable;

public class Product implements Cloneable, Serializable {
    private static int nextId = 1; // статическое поле для генерации ID
    private static synchronized int getNextId() { // синхронизированный метод
        return nextId++;
    }

    private final int id;
    private double price;
    private String name;
    private String description;
    private String brand;
    private String category;

    public Product(String name, double price, String description, String brand, String category) {
        this.id = getNextId();
        this.name = name;
        this.price = price;
        this.description = description;
        this.brand = brand;
        this.category = category;
    }

    public Product(String name, double price) {
        this.id = getNextId();
        this.name = name;
        this.price = price;
        this.description = "";
        this.brand = "";
        this.category = "";
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getBrand() { return brand; }
    public String getCategory() { return category; }

    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setCategory(String category) { this.category = category; }

    public String toShortString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\t").append(price);
        if (brand != null && !brand.isEmpty()) sb.append("\tБренд: ").append(brand);
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id).append("\n");
        sb.append("Название: ").append(name).append("\n");
        sb.append("Цена: ").append(price).append("\n");
        if (description != null && !description.isEmpty()) sb.append("Описание: ").append(description).append("\n");
        if (brand != null && !brand.isEmpty()) sb.append("Бренд: ").append(brand).append("\n");
        if (category != null && !category.isEmpty()) sb.append("Категория: ").append(category);
        return sb.toString();
    }

    @Override
    public Product clone() {
        try {
            return (Product) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
