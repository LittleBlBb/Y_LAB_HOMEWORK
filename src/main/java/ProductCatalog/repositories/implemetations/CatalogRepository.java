package ProductCatalog.repositories.implemetations;

import ProductCatalog.annotations.Performance;
import ProductCatalog.models.Catalog;
import ProductCatalog.repositories.interfaces.ICatalogRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatalogRepository implements ICatalogRepository {
    private final DataSource dataSource;

    public CatalogRepository(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Performance
    public List<Catalog> findAll() {
        List<Catalog> catalogsList = new ArrayList<>();
        final String SQL = "SELECT id, name FROM app.catalog;";

        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            ResultSet resultSet = preparedStatement.executeQuery()){

            while (resultSet.next()){
                catalogsList.add(new Catalog(
                        resultSet.getLong("id"),
                        resultSet.getString("name")
                ));
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Ошибка при чтении catalogs: " +
                    exception.getMessage(), exception);
        }
        return catalogsList;
    }

    @Performance
    public Catalog save(Catalog catalog){
        final String SQL = """
            INSERT INTO app.catalog (name)
            VALUES (?)
            RETURNING id
            """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {

            preparedStatement.setString(1, catalog.getName());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                catalog.setId(resultSet.getLong("id"));
            }
            return catalog;
        } catch (SQLException exception) {
            System.err.println("Ошибка при записи в catalogs: " + exception.getMessage());
            return null;
        }
    }

    @Performance
    public boolean delete(long id) {
        final String SQL = "DELETE FROM app.catalog WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {

            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException exception) {
            System.err.println("Ошибка при удалении catalog: " + exception.getMessage());
            return false;
        }
    }

    @Performance
    public Catalog findById(long id){
        final String SQL = """
                SELECT id, name FROM app.catalog
                WHERE id = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                return new Catalog(
                        resultSet.getLong("id"),
                        resultSet.getString("name")
                );
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Ошибка при чтении catalogs: " +
                    exception.getMessage(), exception);
        }
        return null;
    }
}
