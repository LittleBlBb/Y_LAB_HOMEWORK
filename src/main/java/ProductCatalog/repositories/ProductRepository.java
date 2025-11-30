package ProductCatalog.repositories;

import ProductCatalog.models.Product;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private static final String SQL_FIND_BY_ID = """
                SELECT id, catalog_id, name, price, brand, category, description
                FROM app.product
                WHERE id = ?;
                """;

    private static final String SQL_FIND_ALL = "SELECT * FROM app.product;";

    private static final String SQL_DELETE = "DELETE FROM app.product WHERE id = ?;";

    private static final String SQL_UPDATE = """
            UPDATE app.product
            SET name=?, price=?, description=?, brand=?, category=?
            WHERE id=?;
        """;

    private static final String SQL_INSERT = """
                INSERT INTO app.product (catalog_id, name, price, description, brand, category)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id;
                """;

    private static final String SQL_FIND_BY_CATALOG_ID = """
                SELECT id, catalog_id, name, price, description, brand, category
                FROM app.product
                WHERE catalog_id = ?;
                """;

    private final DataSource dataSource;

    public ProductRepository(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public List<Product> findByCatalogId(long catalogId){
        List<Product> productList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_CATALOG_ID)){

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
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT)){

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
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_UPDATE)) {

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
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_DELETE)) {

            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
        return false;
    }

    public List<Product> findAll() {
        List<Product> productsList = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_ALL)){

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
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_ID)) {
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