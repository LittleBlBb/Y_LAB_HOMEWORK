package ProductCatalog.Repositories;

import ProductCatalog.Models.Product;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private final DataSource dataSource;

    public ProductRepository(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public List<Product> findByCatalog(long catalogId){
        List<Product> productList = new ArrayList<>();

        final String SQL = """
                SELECT id, catalog_id, name, price, description, brand, category
                FROM app.product
                WHERE catalog_id = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)){

            preparedStatement.setLong(1, catalogId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                productList.add(new Product(
                        resultSet.getLong("id"),
                        resultSet.getLong("catalog_id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        resultSet.getString("description"),
                        resultSet.getString("brand"),
                        resultSet.getString("category")
                ));
            }
        }catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
        return productList;
    }

    public Product save(Product p){
        final String SQL = """
                INSERT INTO app.product (catalog_id, name, price, description, brand, category)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)){

            preparedStatement.setLong(1, p.getCatalogId());
            preparedStatement.setString(2, p.getName());
            preparedStatement.setDouble(3, p.getPrice());
            preparedStatement.setString(4, p.getDescription());
            preparedStatement.setString(5, p.getBrand());
            preparedStatement.setString(6, p.getCategory());

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                p.setId(resultSet.getLong("id"));
            }
            return p;
        }catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
        return null;
    }
    public boolean update(Product p) {
        final String SQL = """
            UPDATE app.product
            SET name=?, price=?, description=?, brand=?, category=?
            WHERE id=?
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SQL)) {

            preparedStatement.setString(1, p.getName());
            preparedStatement.setDouble(2, p.getPrice());
            preparedStatement.setString(3, p.getDescription());
            preparedStatement.setString(4, p.getBrand());
            preparedStatement.setString(5, p.getCategory());
            preparedStatement.setLong(6, p.getId());

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
        return false;
    }

    public boolean delete(long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM app.product WHERE id = ?")) {

            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
        return false;
    }

    public List<Product> findAll() {
        final String SQL = "SELECT * FROM app.product";
        List<Product> productsList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)){

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                productsList.add(new Product(
                        resultSet.getLong("id"),
                        resultSet.getLong("catalog_id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        resultSet.getString("description"),
                        resultSet.getString("brand"),
                        resultSet.getString("category")
                ));

            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return productsList;
    }

    public Product findById(long id){
        final String SQL = """
                SELECT id, catalog_id, name, price, brand, category, description
                FROM app.product
                WHERE id = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                return new Product(
                        resultSet.getLong("id"),
                        resultSet.getLong("catalog_id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        resultSet.getString("brand"),
                        resultSet.getString("category"),
                        resultSet.getString("description")
                );
            }
        } catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
        return null;
    }
}