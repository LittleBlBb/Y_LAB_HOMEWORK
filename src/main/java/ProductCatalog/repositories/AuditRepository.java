package ProductCatalog.repositories;

import ProductCatalog.models.AuditEntry;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuditRepository {
    private final DataSource dataSource;

    private static final String SQL_INSERT = """
            INSERT INTO app.audit_log (username, action, details, timestamp)
            VALUES (?, ?, ?, NOW())
            RETURNING id;
            """;

    private static final String SQL_FIND_ALL = """
            SELECT * FROM app.audit_log;
            """;

    private static final String SQL_FIND_BY_ID = """
            SELECT * FROM app.audit_log
            WHERE id = ?;
            """;

    public AuditRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public AuditEntry save(AuditEntry entry){
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT)){
            preparedStatement.setString(1, entry.getUsername());
            preparedStatement.setString(2, entry.getAction());
            preparedStatement.setString(3, entry.getDetails());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                entry.setId(resultSet.getLong("id"));
            }
            return entry;
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
        return null;
    }

    public List<AuditEntry> findAll() {
        List<AuditEntry> logsList = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_ALL);
            ResultSet resultSet = preparedStatement.executeQuery()){

            while (resultSet.next()){
                logsList.add(new AuditEntry(
                        resultSet.getLong("id"),
                        resultSet.getString("username"),
                        resultSet.getString("action"),
                        resultSet.getString("details"),
                        resultSet.getTimestamp("timestamp").toLocalDateTime()
                ));
            }
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
        return logsList;
    }

    public AuditEntry findById(long id){
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_ID)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                return new AuditEntry(
                        resultSet.getLong("id"),
                        resultSet.getString("username"),
                        resultSet.getString("action"),
                        resultSet.getString("details"),
                        resultSet.getTimestamp("timestamp").toLocalDateTime()
                );
            }
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
        return null;
    }
}
