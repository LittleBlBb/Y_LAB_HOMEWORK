package ProductCatalog.services.implementations;

import ProductCatalog.constants.Role;
import ProductCatalog.models.User;
import ProductCatalog.repositories.interfaces.IUserRepository;
import ProductCatalog.services.interfaces.IUserService;

import java.util.List;

/**
 * Сервис для управления пользователями.
 * Поддерживает регистрацию, вход, выход и проверку ролей.
 */
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private User currentUser;

    /**
     * Создает экземпляр {@code UserService}.
     *
     * @param userRepository объект, управляющий пользователями из БД
     */
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    /**
     * Регистрирует нового пользователя.
     *
     * @param username имя пользователя
     * @param password пароль
     * @return {@code true}, если регистрация успешна
     */
    public boolean register(String username, String password) {
        if (userRepository.findByUsername(username) != null) {
            return false;
        }
        User newUser = new User(username, password, Role.USER);
        userRepository.save(newUser);

        return true;
    }
    /**
     * Регистрирует нового пользователя.
     *
     * @param user новый пользователь
     * @return {@code true}, если регистрация успешна
     */
    public boolean register(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return false;
        }
        userRepository.save(user);
        return true;
    }

    /**
     * Авторизует пользователя.
     *
     * @param username имя пользователя
     * @param password пароль
     * @return {@code true}, если вход успешен
     */
    public boolean login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    /**
     * Выходит из текущего аккаунта.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Проверяет, авторизован ли пользователь.
     *
     * @return {@code true}, если пользователь вошёл в систему
     */
    public boolean isAuthenticated() {
        return currentUser != null;
    }

    /**
     * Проверяет, является ли пользователь администратором.
     *
     * @return {@code true}, если роль пользователя — admin
     */
    public boolean isAdmin() {
        return currentUser != null && Role.ADMIN.equals(currentUser.getRole());
    }

    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean checkPassword(User user, String password){
        if (user.getPassword().equals(password)){
            return true;
        }
        return false;
    }
}
