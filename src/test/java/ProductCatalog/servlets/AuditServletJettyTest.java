package ProductCatalog.servlets;

import ProductCatalog.constants.Permission;
import ProductCatalog.constants.Role;
import ProductCatalog.dto.AuditEntryDTO;
import ProductCatalog.dto.LoginRequest;
import ProductCatalog.models.AuditEntry;
import ProductCatalog.models.User;
import ProductCatalog.services.implemetations.AuditService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuditServletJettyTest {

    private static Server server;
    private static int port = 8082;
    private static UserService mockUserService;
    private static AuditService mockAuditService;
    private static ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void startServer() throws Exception {
        mockUserService = mock(UserService.class);
        mockAuditService = mock(AuditService.class);

        server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setAttribute("userService", mockUserService);
        context.setAttribute("auditService", mockAuditService);

        context.addServlet(new ServletHolder(new LoginServlet()), "/login");
        context.addServlet(new ServletHolder(new AuditServlet()), "/logs");

        server.setHandler(context);
        server.start();
    }

    @AfterAll
    static void stopServer() throws Exception {
        server.stop();
    }

    @BeforeEach
    void resetMocks() {
        reset(mockUserService, mockAuditService);

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
        assertEquals(200, code);

        String setCookie = connection.getHeaderField("Set-Cookie");
        return setCookie.split(";")[0];
    }

    @Test
    void testDoGetLogs() throws Exception {
        AuditEntry entry = new AuditEntry(1, "admin", "login", "successful", LocalDateTime.now());
        when(mockAuditService.getAll()).thenReturn(List.of(entry));

        String sessionCookie = loginAsAdmin();

        URL url = new URL("http://localhost:" + port + "/logs");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Cookie", sessionCookie);

        int code = connection.getResponseCode();
        assertEquals(200, code);

        try (InputStream is = connection.getInputStream()) {
            List<Map<String, Object>> responseList = mapper.readValue(is, List.class);
            assertEquals(1, responseList.size());
            assertEquals("admin", responseList.get(0).get("username"));
            assertEquals("login", responseList.get(0).get("action"));
            assertEquals("successful", responseList.get(0).get("details"));
        }
    }
}
