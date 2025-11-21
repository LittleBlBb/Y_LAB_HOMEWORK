package ProductCatalog.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Catalog {
    private long id;
    private String name;

    public Catalog(String name) {
        this.name = name;
    }
}
