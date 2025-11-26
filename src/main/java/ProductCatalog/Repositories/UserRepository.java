package ProductCatalog.Repositories;

import ProductCatalog.Models.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
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

    public User findByUsername(String username) {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_USERNAME)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new User(
                        resultSet.getLong("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("role")
                );
            }
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
        return null;
    }

    public User save(User user){
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT)){

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getRole());

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                user.setId(resultSet.getLong("id"));
            }

            return user;
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
        return null;
    }

    public List<User> findAll() {
        List<User> usersList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_ALL)){

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                usersList.add(new User(
                        resultSet.getLong("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("role")
                ));

            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return usersList;
    }

    public User findById(long id){
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_ID)) {
                preparedStatement.setLong(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next()){
                    return new User(
                            resultSet.getLong("id"),
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("role")
                    );
                }
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
        return null;
    }
}
