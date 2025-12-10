package ProductCatalog.repositories.implemetations;

import ProductCatalog.performance.annotations.Performance;
import ProductCatalog.models.Product;
import ProductCatalog.repositories.interfaces.IProductRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Репозиторий для управления товарами.
 * Отвечает за действия с товарами в бд
 */
public class ProductRepository implements IProductRepository {
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

    /**
     * Ищет все товары из каталога по catalogId
     * @param catalogId
     * @return каталог
     */
    @Performance
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

    /**
     * Сохраняет товар в бд
     * @param product
     * @return целый товар
     */
    @Performance
    public Product save(Product product){
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT)){

            preparedStatement.setLong(1, product.getCatalogId());
            preparedStatement.setString(2, product.getName());
            preparedStatement.setDouble(3, product.getPrice());
            preparedStatement.setString(4, product.getDescription());
            preparedStatement.setString(5, product.getBrand());
            preparedStatement.setString(6, product.getCategory());

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                product.setId(resultSet.getLong("id"));
            }
            return product;
        }catch (SQLException exception){
            System.out.println(exception.getMessage());
        }
        return null;
    }

    /**
     * Обновляет товар по id в бд
     * @param product
     * @return true, если обновилось, иначе false.
     */
    @Performance
    public boolean update(Product product) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_UPDATE)) {

            preparedStatement.setString(1, product.getName());
            preparedStatement.setDouble(2, product.getPrice());
            preparedStatement.setString(3, product.getDescription());
            preparedStatement.setString(4, product.getBrand());
            preparedStatement.setString(5, product.getCategory());
            preparedStatement.setLong(6, product.getId());

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
        return false;
    }

    /**
     * Удаляет товар из бд по id
     * @param id
     * @return true, если удалилось, иначе true.
     */
    @Performance
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

    /**
     * Получает все товары из бд
     * @return список товаров
     */
    @Performance
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

    /**
     * Ищет товар по id в бд
     * @param id
     * @return найденный товар
     */
    @Performance
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