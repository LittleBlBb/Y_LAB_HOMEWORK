package ProductCatalog.Services;

import ProductCatalog.Models.User;
import ProductCatalog.UnitOfWork;

public class UserService {
    private static UserService instance;
    private final UnitOfWork unitOfWork;
    private User currentUser;

    public UserService() {
        this.unitOfWork = UnitOfWork.getInstance();
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public boolean register(String username, String password) {
        if (unitOfWork.findUserByUsername(username) != null) {
            return false;
        }

        User newUser = new User(username, password, "user");
        unitOfWork.addUser(newUser);
        return true;
    }

    public boolean login(String username, String password) {
        User user = unitOfWork.findUserByUsername(username);
        if (user != null && user.getPassword().equals(password)){
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logout(){
        currentUser = null;
    }

    public User getCurrentUser(){
        return currentUser;
    }

    public boolean isAuthenticated(){
        return currentUser != null;
    }

    public boolean isAdmin(){
        return currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole());
    }
}