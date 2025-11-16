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
    private final DataSource dataSource;

    public CatalogRepository(DataSource dataSource){
        this.dataSource = dataSource;
    }

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
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
        return catalogsList;
    }

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
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
        return null;
    }


    public boolean delete(long id) {
        final String SQL = "DELETE FROM app.catalog WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)){

            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
        return false;
    }

    public Catalog findById(long id){
        List<Catalog> catalogsList = this.findAll();
        for (Catalog c : catalogsList){
            if(c.getId() == id) return c;
        }
        return null;
    }
}
