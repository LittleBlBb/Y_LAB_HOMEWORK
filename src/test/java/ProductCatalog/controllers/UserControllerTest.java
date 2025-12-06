//package ProductCatalog.controllers;
//
//import ProductCatalog.constants.Role;
//import ProductCatalog.dto.UserDTO;
//import ProductCatalog.models.User;
//import ProductCatalog.services.implementations.UserService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//public class UserControllerTest {
//
//    private UserService userService;
//    private MockMvc mockMvc;
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    @BeforeEach
//    void setUp() {
//        userService = Mockito.mock(UserService.class);
//        UserController controller = new UserController(userService);
//        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
//    }
//
//    @Test
//    void testGetAllUsers() throws Exception {
//        when(userService.getAll())
//                .thenReturn(List.of(new User(0L, "admin", "admin", Role.ADMIN)));
//
//        mockMvc.perform(get("/api/users"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].username").value("admin"));
//    }
//
//    @Test
//    void testRegisterUserSuccess() throws Exception {
//        UserDTO dto = new UserDTO(0L, "user", "123");
//
//        when(userService.register(any())).thenReturn(true);
//
//        mockMvc.perform(
//                        post("/api/users")
//                                .contentType("application/json")
//                                .content(mapper.writeValueAsString(dto))
//                )
//                .andExpect(status().isOk())
//                .andExpect(content().string("REGISTERED"));
//    }
//}
