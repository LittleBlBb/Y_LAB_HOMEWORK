package ProductCatalog.Repository;

import ProductCatalog.Models.User;
import ProductCatalog.Repositories.AuditRepository;
import ProductCatalog.Repositories.UserRepository;
import org.junit.jupiter.api.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserRepositoryTest extends AbstractRepositoryTest {

    private UserRepository userRepository;

    @BeforeEach
    void init() {
        userRepository = new UserRepository(dataSource);
    }

    @Test
    public void testInsertAndFind() {
        User savedUser = new User("testUser", "testPassword", "user");
        userRepository.save(savedUser);

        User fetchedUser = userRepository.findById(savedUser.getId());

        Assertions.assertArrayEquals(
                new String[]{"testUser", "testPassword", "user"},
                new String[]{fetchedUser.getUsername(), fetchedUser.getPassword(), fetchedUser.getRole()}
        );
    }
}
