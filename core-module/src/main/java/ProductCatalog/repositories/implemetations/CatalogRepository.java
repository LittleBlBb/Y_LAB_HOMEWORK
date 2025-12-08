package ProductCatalog.repositories.implemetations;

import ProductCatalog.performance.annotations.Performance;
import ProductCatalog.models.Catalog;
import ProductCatalog.repositories.interfaces.ICatalogRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Репозиторий для управления каталогами.
 * Отвечает за действия с каталогами в бд
 */
public class CatalogRepository implements ICatalogRepository {
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

    /**
     * Получает все каталоги из бд
     * @return список каталогов
     */
    @Performance
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

    /**
     * Сохраняет каталог в бд
     * @param catalog
     * @return целый каталог
     */
    @Performance
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

    /**
     * Удаляет каталог из бд
     * @param id
     * @return true, если удалилось, иначе false.
     */
    @Performance
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

    /**
     * Ищет каталог по id в бд
     * @param id
     * @return каталог
     */
    @Performance
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