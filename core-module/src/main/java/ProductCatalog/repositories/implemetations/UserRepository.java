package ProductCatalog.repositories.implemetations;

import ProductCatalog.performance.annotations.Performance;
import ProductCatalog.constants.Role;
import ProductCatalog.models.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Репозиторий для управления пользователями.
 * Отвечает за действия с пользователями в бд
 */
public class UserRepository implements ProductCatalog.repositories.interfaces.IUserRepository {
    private static final String SQL_FIND_BY_USERNAME = """
                SELECT id, username, password, role
                FROM app.user
                WHERE username = ?;
                """;

    private static final String SQL_INSERT = """
                INSERT INTO app.user (username, password, role)
                VALUES (?, ?, ?)
                RETURNING id;
                """;

    private static final String SQL_FIND_ALL = "SELECT id, username, password, role FROM app.user;";

    private static final String SQL_FIND_BY_ID = """
                SELECT id, username, password, role FROM app.user
                WHERE id = ?
                """;

    private final DataSource dataSource;

    public UserRepository(DataSource dataSource){
        this.dataSource = dataSource;
    }

    /**
     * Ищет пользователя по имени в бд
     * @param username
     * @return найденный пользователь
     */
    @Performance
    public User findByUsername(String username) {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_USERNAME)) {
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

    /**
     * Сохраняет пользователя в бд
     * @param user
     * @return целый пользователь
     */
    @Performance
    public User save(User user){
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT)){

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

    /**
     * Получает всех пользователей из бд
     * @return список пользователей
     */
    @Performance
    public List<User> findAll() {
        List<User> usersList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_ALL)){

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

    /**
     * Ищет пользователя по id в бд
     * @param id
     * @return найденный пользователь
     */
    @Performance
    public User findById(long id){
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_ID)) {
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
