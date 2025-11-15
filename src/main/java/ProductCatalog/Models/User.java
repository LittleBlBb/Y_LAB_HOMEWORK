package ProductCatalog.Models;

import lombok.Data;


@Data
public class User {
    private long id;
    private String username;
    private String password;
    private String role;

    public User(long id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
