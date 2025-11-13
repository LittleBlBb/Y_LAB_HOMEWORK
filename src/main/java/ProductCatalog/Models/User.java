package ProductCatalog.Models;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int nextId = 1;
    private final int id;
    private String username;
    private String password;
    private String role;

    public User(String username, String password, String role) {
        id = nextId++;
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
