package ProductCatalog.servlets;

import ProductCatalog.constants.Role;
import ProductCatalog.dto.LoginRequest;
import ProductCatalog.dto.UserDTO;
import ProductCatalog.models.User;
import ProductCatalog.services.implemetations.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.jupiter.api.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserServletTest {

    private static Server server;
    private static int port = 8081;
    private static UserService mockUserService;
    private static ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void startServer() throws Exception {
        mockUserService = mock(UserService.class);

        server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setAttribute("userService", mockUserService);

        context.addServlet(new ServletHolder(new LoginServlet()), "/login");
        context.addServlet(new ServletHolder(new UserServlet()), "/users");

        server.setHandler(context);
        server.start();
    }

    @AfterAll
    static void stopServer() throws Exception {
        server.stop();
    }

    @BeforeEach
    void resetMocks() {
        reset(mockUserService);

        // Мок для логина admin
        when(mockUserService.findByUsername("admin"))
                .thenReturn(new User(1, "admin", "admin", Role.ADMIN));
    }

    private String loginAsAdmin() throws Exception {
        URL url = new URL("http://localhost:" + port + "/login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        LoginRequest login = new LoginRequest();
        login.setUsername("admin");
        login.setPassword("admin");

        try (OutputStream os = connection.getOutputStream()) {
            mapper.writeValue(os, login);
        }

        int code = connection.getResponseCode();
        assertEquals(200, code, "Login should return 200");

        String setCookie = connection.getHeaderField("Set-Cookie");
        return setCookie.split(";")[0]; // JSESSIONID=...
    }

    @Test
    void testDoGetWithLogin() throws Exception {
        User user = new User(2, "testuser", "pass", Role.ADMIN);
        when(mockUserService.getAll()).thenReturn(List.of(user));

        String sessionCookie = loginAsAdmin();

        URL url = new URL("http://localhost:" + port + "/users");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Cookie", sessionCookie);

        int code = connection.getResponseCode();
        assertEquals(200, code);

        try (InputStream is = connection.getInputStream()) {
            List<Map<String, Object>> responseList = mapper.readValue(is, List.class);
            assertEquals(1, responseList.size());
            assertEquals("testuser", responseList.get(0).get("username"));
        }
    }

    @Test
    void testDoPost() throws Exception {
        UserDTO dto = new UserDTO(0, "newuser", "12345");
        when(mockUserService.register(any(User.class))).thenReturn(true);

        URL url = new URL("http://localhost:" + port + "/users");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = connection.getOutputStream()) {
            mapper.writeValue(os, dto);
        }

        int code = connection.getResponseCode();
        assertEquals(201, code);

        try (InputStream is = connection.getInputStream()) {
            UserDTO response = mapper.readValue(is, UserDTO.class);
            assertEquals("newuser", response.getUsername());
        }
    }
}
