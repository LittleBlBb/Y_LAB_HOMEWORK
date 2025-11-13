package ProductCatalog.Services;

import ProductCatalog.Models.User;
import ProductCatalog.UnitOfWork;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private final UnitOfWork unitOfWork;
    private final AuditService auditService;
    @Getter
    private User currentUser;

    public UserService(UnitOfWork unitOfWork, AuditService auditService) {
        this.unitOfWork = unitOfWork;
        this.auditService = auditService;
    }

    public boolean register(String username, String password) {
        if (findUserByUsername(username) != null) {
            return false;
        }
        User newUser = new User(username, password, "user");
        unitOfWork.getUsers().add(newUser);
        auditService.logAction(username, "REGISTER", "Регистрация нового пользователя");
        return true;
    }

    public boolean login(String username, String password) {
        User user = findUserByUsername(username);
        if (user != null && user.getPassword().equals(password)){
            currentUser = user;
            auditService.logAction(username, "LOGIN", "Успешный вход");
            return true;
        }
        auditService.logAction(username, "LOGIN_FAILED", "Неудачная попытка входа");
        return false;
    }

    public void logout(){
        if (currentUser != null) {
            auditService.logAction(currentUser.getUsername(), "LOGOUT", "Выход из системы");
        }
        currentUser = null;
    }

    public boolean isAuthenticated(){
        return currentUser != null;
    }

    public boolean isAdmin(){
        return currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole());
    }
    private User findUserByUsername(String username){
        for (User u : unitOfWork.getUsers()){
            if (u.getUsername().equalsIgnoreCase(username)){
                return u;
            }
        }
        return null;
    }
}