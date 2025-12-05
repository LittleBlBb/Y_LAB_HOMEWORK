package ProductCatalog.repositories.interfaces;

import ProductCatalog.models.User;

import java.util.List;

public interface IUserRepository {
    User findByUsername(String username);
    User findById(long id);
    List<User> findAll();
    User save(User user);
}
