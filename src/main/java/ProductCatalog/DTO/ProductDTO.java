package ProductCatalog.DTO;

import lombok.Data;

@Data
public class ProductDTO {
    private long id;
    private long catalogId;
    private String name;
    private double price;
    private String description;
    private String brand;
    private String category;

}