package ProductCatalog.repositories.implemetations;

import ProductCatalog.annotations.Performance;
import ProductCatalog.models.Role;
import ProductCatalog.models.User;
import ProductCatalog.repositories.interfaces.IUserRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IUserRepository {
    private final DataSource dataSource;

    public UserRepository(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Performance
    public User findByUsername(String username) {
        final String SQL = """
                SELECT id, username, password, role
                FROM app.user
                WHERE username = ?
                """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String roleStr = resultSet.getString("role");
                Role role = Role.valueOf(roleStr.toUpperCase());
                return new User(
                        resultSet.getLong("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        role
                );
            }
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
        return null;
    }

    @Performance
    public User save(User user){
        final String SQL = """
                INSERT INTO app.user (username, password, role)
                VALUES (?, ?, ?)
                RETURNING id
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)){

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getRole().toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                user.setId(resultSet.getLong("id"));
            }

            return user;
        } catch (SQLException exception) {
            System.err.println("Ошибка при удалении: " + exception.getMessage());
            return null;
        }
    }

    @Performance
    public List<User> findAll() {
        final String SQL = "SELECT id, username, password, role FROM app.user";
        List<User> usersList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL)){

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String roleStr = resultSet.getString("role");
                Role role = Role.valueOf(roleStr.toUpperCase());
                usersList.add(new User(
                        resultSet.getLong("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        role
                ));

            }
        } catch (SQLException exception) {
            throw new RuntimeException("Ошибка при чтении users: " +
                    exception.getMessage(), exception);
        }
        return usersList;
    }

    @Performance
    public User findById(long id){
        final String SQL = """
                SELECT id, username, password, role FROM app.user
                WHERE id = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
                preparedStatement.setLong(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next()){
                    String roleStr = resultSet.getString("role");
                    Role role = Role.valueOf(roleStr.toUpperCase());
                    return new User(
                            resultSet.getLong("id"),
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            role
                    );
                }
        } catch (SQLException exception) {
            throw new RuntimeException("Ошибка при чтении users: " +
                    exception.getMessage(), exception);
        }
        return null;
    }
}
