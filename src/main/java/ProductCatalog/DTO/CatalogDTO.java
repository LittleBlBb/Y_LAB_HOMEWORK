package ProductCatalog.DTO;

public class CatalogDTO {
    private long id;
    private String name;

    public CatalogDTO() {}

    public CatalogDTO(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
