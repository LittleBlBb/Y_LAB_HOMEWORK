package ProductCatalog.controllers;

import ProductCatalog.constants.Permission;
import ProductCatalog.constants.Role;
import ProductCatalog.dto.UserDTO;
import ProductCatalog.models.User;
import ProductCatalog.services.implementations.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.file.AccessDeniedException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest extends BaseControllerTest {

    private UserService userService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        super.setUpBase();
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    // ---------------------------------------------------------
    // GET ALL USERS
    // ---------------------------------------------------------
    @Test
    void getAllUsers_returnsMappedDTOs_whenUserHasPermission() throws Exception {
        User u1 = new User(1L, "user1", "pass1", Role.USER);
        User u2 = new User(2L, "user2", "pass2", Role.USER);

        when(userService.getAll()).thenReturn(List.of(u1, u2));

        var result = userController.getAllUsers(request);

        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        verify(userService).getAll();
    }

    @Test
    void getAllUsers_throwsAccessDenied_whenUserNotLoggedIn() {
        assertThrows(AccessDeniedException.class, () ->
                userController.getAllUsers(createNotLoggedInRequest())
        );
    }

    @Test
    void getAllUsers_throwsAccessDenied_whenNoPermission() {
        User normalUser = new User(2L, "user", "pass", Role.USER);

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
                userController.getAllUsers(createUserRequest(normalUser))
        );

        assertEquals("You do not have permission: " + Permission.MANAGE_USERS, ex.getMessage());
    }

    // ---------------------------------------------------------
    // REGISTER USER
    // ---------------------------------------------------------
    @Test
    void registerUser_returns200_whenSuccess() {
        UserDTO dto = new UserDTO(0, "newuser", "pass");

        when(userService.register(any(User.class))).thenReturn(true);

        ResponseEntity<?> result = userController.registerUser(dto);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Registered successfully", result.getBody());
        verify(userService).register(any(User.class));
    }

    @Test
    void registerUser_returns400_whenValidationFails() {
        UserDTO dto = new UserDTO(0, "", ""); // обе строки пустые → должны быть ошибки

        ResponseEntity<?> result = userController.registerUser(dto);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody() instanceof List);
        assertFalse(((List<?>) result.getBody()).isEmpty());
        verify(userService, never()).register(any(User.class));
    }

    @Test
    void registerUser_returns400_whenServiceFails() {
        UserDTO dto = new UserDTO(0, "newuser", "pass");

        when(userService.register(any(User.class))).thenReturn(false);

        ResponseEntity<?> result = userController.registerUser(dto);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Failed to create user", result.getBody());
        verify(userService).register(any(User.class));
    }
}