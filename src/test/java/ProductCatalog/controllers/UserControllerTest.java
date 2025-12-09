package ProductCatalog.controllers;

import ProductCatalog.constants.Permission;
import ProductCatalog.constants.Role;
import ProductCatalog.models.User;
import ProductCatalog.services.implementations.UserService;
import ProductCatalog.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.AccessDeniedException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest extends BaseControllerTest {

    private UserService userService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        super.setUpBase();
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    void getAllUsers_returnsMappedDTOs_whenUserHasPermission() throws Exception {
        User u1 = new User(1L, "user1", "pass1", Role.USER);
        User u2 = new User(2L, "user2", "pass2", Role.USER);

        when(userService.getAll()).thenReturn(List.of(u1, u2));

        var result = userController.getAllUsers(request);

        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());

        verify(userService, times(1)).getAll();
    }

    @Test
    void getAllUsers_throwsAccessDeniedException_whenUserNotLoggedIn() {
        assertThrows(AccessDeniedException.class, () -> {
            userController.getAllUsers(createNotLoggedInRequest());
        });
    }

    @Test
    void getAllUsers_throwsAccessDeniedException_whenUserHasNoPermission() {
        User normalUser = new User(2L, "user", "pass", Role.USER);

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            userController.getAllUsers(createUserRequest(normalUser));
        });

        assertEquals("You do not have permission: " + Permission.MANAGE_USERS, ex.getMessage());
    }

    @Test
    void registerUser_returnsRegistered_whenDataValid() {
        UserDTO dto = new UserDTO(0, "newuser", "pass");
        when(userService.register(any(User.class))).thenReturn(true);

        String result = userController.registerUser(dto);

        assertEquals("REGISTERED", result);
        verify(userService, times(1)).register(any(User.class));
    }

    @Test
    void registerUser_returnsFailed_whenServiceFails() {
        UserDTO dto = new UserDTO(0, "newuser", "pass");
        when(userService.register(any(User.class))).thenReturn(false);

        String result = userController.registerUser(dto);

        assertEquals("FAILED", result);
        verify(userService, times(1)).register(any(User.class));
    }
}
