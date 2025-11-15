package ProductCatalog.Repositories;

import ProductCatalog.Models.AuditEntry;
import ProductCatalog.Models.Catalog;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuditRepository {
    private final DataSource dataSource;

    public AuditRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(AuditEntry entry){
        final String SQL = """
                INSERT INTO app.audit_log (username, action, details, timestamp)
                VALUES (?, ?, ?, NOW())
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)){
            preparedStatement.setString(1, entry.getUsername());
            preparedStatement.setString(2, entry.getAction());
            preparedStatement.setString(3, entry.getDetails());

            preparedStatement.executeUpdate();
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
    }

    public List<AuditEntry> findAll() {
        List<AuditEntry> logsList = new ArrayList<>();
        final String SQL = "SELECT username, action, details, timestamp FROM app.audit_log";

        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            ResultSet resultSet = preparedStatement.executeQuery()){

            while (resultSet.next()){
                logsList.add(new AuditEntry(
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
}
