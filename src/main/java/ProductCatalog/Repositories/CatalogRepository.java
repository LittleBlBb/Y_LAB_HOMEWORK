package ProductCatalog.Repositories;

import ProductCatalog.Models.Catalog;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatalogRepository {
    private static final String SQL_FIND_ALL = """
            SELECT id, name FROM app.catalog;
            """;

    private static final String SQL_FIND_BY_ID = """
                SELECT id, name FROM app.catalog
                WHERE id = ?
                """;

    private static final String SQL_DELETE = """
            DELETE FROM app.catalog WHERE id = ?;
            """;

    private static final String SQL_INSERT = """
            INSERT INTO app.catalog (name)
            VALUES (?)
            RETURNING id;
            """;

    private final DataSource dataSource;

    public CatalogRepository(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public List<Catalog> findAll() {
        List<Catalog> catalogsList = new ArrayList<>();

        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_ALL);
            ResultSet resultSet = preparedStatement.executeQuery()){

            while (resultSet.next()){
                catalogsList.add(new Catalog(
                        resultSet.getLong("id"),
                        resultSet.getString("name")
                ));
            }
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
        return catalogsList;
    }

    public Catalog save(Catalog catalog){
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT)) {

            preparedStatement.setString(1, catalog.getName());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                catalog.setId(resultSet.getLong("id"));
            }
            return catalog;
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
        return null;
    }

    public boolean delete(long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE)){

            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
        return false;
    }

    public Catalog findById(long id){
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_ID)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                return new Catalog(
                        resultSet.getLong("id"),
                        resultSet.getString("name")
                );
            }
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
        return null;
    }
}