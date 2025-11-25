package ProductCatalog.Models;

import lombok.Data;

@Data
public class Catalog {
    private long id;
    private String name;

    public Catalog(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Catalog(String name) {
        this.name = name;
    }
}