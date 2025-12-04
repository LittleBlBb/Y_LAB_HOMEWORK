package ProductCatalog.controllers;

import ProductCatalog.dto.UserDTO;
import ProductCatalog.models.User;
import ProductCatalog.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {

    private UserService userService;
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        UserController controller = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userService.getAll())
                .thenReturn(List.of(new User(1L, "admin", "123", "ROLE_ADMIN")));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].role").value("ROLE_ADMIN"));
    }

    @Test
    void testRegisterUserSuccess() throws Exception {
        UserDTO dto = new UserDTO(0L, "user", "123", "ROLE_USER");

        when(userService.register(any())).thenReturn(true);

        mockMvc.perform(
                        post("/api/users")
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(content().string("REGISTERED"));
    }
}
