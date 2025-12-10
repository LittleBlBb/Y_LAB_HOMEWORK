package ProductCatalog.models;

public class Catalog {
    private long id;
    private String name;
    public Catalog(long id, String name) {
        this.id = id;
        this.name = name;
    }
    public Catalog() {}
    public Catalog(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
