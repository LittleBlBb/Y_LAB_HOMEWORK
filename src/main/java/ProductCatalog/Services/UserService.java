package ProductCatalog.Services;

import ProductCatalog.Models.User;
import ProductCatalog.UnitOfWork;
import lombok.Getter;

import java.util.List;

/**
 * Сервис для управления пользователями.
 * Поддерживает регистрацию, вход, выход и проверку ролей.
 */
public class UserService {
    private final UnitOfWork unitOfWork;
    private final AuditService auditService;
    @Getter
    private User currentUser;

    /**
     * Создает экземпляр {@code UserService}.
     *
     * @param unitOfWork объект, управляющий данными приложения
     * @param auditService сервис аудита
     */
    public UserService(UnitOfWork unitOfWork, AuditService auditService) {
        this.unitOfWork = unitOfWork;
        this.auditService = auditService;
    }

    /**
     * Регистрирует нового пользователя.
     *
     * @param username имя пользователя
     * @param password пароль
     * @return {@code true}, если регистрация успешна
     */
    public boolean register(String username, String password) {
        if (findUserByUsername(username) != null) {
            return false;
        }
        User newUser = new User(username, password, "user");
        unitOfWork.getUsers().add(newUser);
        auditService.logAction(username, "REGISTER", "Регистрация нового пользователя");
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
        User user = findUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            auditService.logAction(username, "LOGIN", "Успешный вход");
            return true;
        }
        auditService.logAction(username, "LOGIN_FAILED", "Неудачная попытка входа");
        return false;
    }

    /**
     * Выходит из текущего аккаунта.
     */
    public void logout() {
        if (currentUser != null) {
            auditService.logAction(currentUser.getUsername(), "LOGOUT", "Выход из системы");
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

    /**
     * Находит пользователя по имени.
     *
     * @param username имя пользователя
     * @return объект {@link User} или {@code null}, если пользователь не найден
     */
    private User findUserByUsername(String username) {
        for (User u : unitOfWork.getUsers()) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }
}
