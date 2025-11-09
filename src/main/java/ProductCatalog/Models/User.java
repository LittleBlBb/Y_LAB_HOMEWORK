package ProductCatalog.Models;

import java.io.Serializable;

public class User implements Serializable {
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

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }
    @Override
    public String toString(){
        return "User{id=" + id + ", username='" + username + "', role='" + role + "'}";
    }
}
