package ProductCatalog.services;

import ProductCatalog.models.User;
import ProductCatalog.repositories.UserRepository;

import java.util.List;

/**
 * Сервис для управления пользователями.
 * Поддерживает регистрацию, вход, выход и проверку ролей.
 */
public class UserService {
    private final UserRepository userRepository;
    private final AuditService auditService;
    private User currentUser;

    /**
     * Создает экземпляр {@code UserService}.
     *
     * @param userRepository объект, управляющий пользователями из БД
     * @param auditService сервис аудита
     */
    public UserService(UserRepository userRepository, AuditService auditService) {
        this.userRepository = userRepository;
        this.auditService = auditService;
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
        User newUser = new User(username, password, "User");
        userRepository.save(newUser);

        auditService.save(username, "REGISTER", "Регистрация нового пользователя");

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
        auditService.save(user.getUsername(), "REGISTER", "Регистрация нового пользователя");
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
            auditService.save(username, "LOGIN", "Успешный вход");
            return true;
        }

        auditService.save(username, "LOGIN_FAILED", "Неудачная попытка входа");
        return false;
    }

    /**
     * Выходит из текущего аккаунта.
     */
    public void logout() {
        if (currentUser != null) {
            auditService.save(currentUser.getUsername(), "LOGOUT", "Выход из системы");
        }
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
        return currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole());
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
