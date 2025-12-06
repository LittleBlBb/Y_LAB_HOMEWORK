package ProductCatalog.repositories;

import ProductCatalog.constants.Role;
import ProductCatalog.models.User;
import ProductCatalog.repositories.implemetations.UserRepository;
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
        User savedUser = new User("testUser", "testPassword", Role.USER);
        userRepository.save(savedUser);

        User fetchedUser = userRepository.findById(savedUser.getId());

        Assertions.assertArrayEquals(
                new String[]{"testUser", "testPassword"},
                new String[]{fetchedUser.getUsername(), fetchedUser.getPassword()}
        );
    }
}
