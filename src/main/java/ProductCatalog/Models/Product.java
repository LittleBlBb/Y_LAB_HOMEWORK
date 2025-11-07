package ProductCatalog.Models;

import java.util.concurrent.atomic.AtomicInteger;

public class Product implements Cloneable {
    private final int id;
    private double price;
    private String name;
    private String description;
    private static final AtomicInteger dynamicProductId = new AtomicInteger(1);

    public Product(String name, double price, String description) {
        this.id = dynamicProductId.getAndIncrement();
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public Product(String name, double price) {
        this.id = dynamicProductId.getAndIncrement();
        this.name = name;
        this.price = price;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String productName) {
        this.name = productName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        if (description == null || description.isEmpty()) {
            return id + "\t" + name + "\t" + price;
        }
        return id + "\t" + name + "\t" + price + "\n" + description;
    }

    @Override
    public Product clone() {
        try {
            return (Product) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // не должно происходить
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
