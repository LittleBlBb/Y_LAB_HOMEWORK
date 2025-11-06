package Product;

public class Product {
    private static int dynamicProductId = 1;
    private int Id;
    private String Name;
    private double price;
    private String description;

    public Product(String productName, double price) {
        this.Id = dynamicProductId;
        this.Name = productName;
        this.price = price;
        dynamicProductId++;
    }

    public Product(String name, double price, String description) {
        Name = name;
        this.price = price;
        this.description = description;
        dynamicProductId++;
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return Name;
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

    public void setProductName(String productName) {
        this.Name = productName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString(){
        return Id + "\t" + Name + "\t" + price;
    }
}
