package ProductCatalog.services.interfaces;

import ProductCatalog.models.User;

import java.util.List;

public interface IUserService {
    User getCurrentUser();
    User findByUsername(String username);
    List<User> getAll();
    boolean register(String username, String password);
    boolean register(User user);
    boolean login(String username, String password);
    void logout();
    boolean isAuthenticated();
    boolean isAdmin();

}
