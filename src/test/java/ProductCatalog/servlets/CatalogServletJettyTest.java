package ProductCatalog.servlets;

import ProductCatalog.constants.Role;
import ProductCatalog.dto.CatalogDTO;
import ProductCatalog.dto.LoginRequest;
import ProductCatalog.models.Catalog;
import ProductCatalog.models.User;
import ProductCatalog.services.implemetations.CatalogService;
import ProductCatalog.services.implemetations.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CatalogServletJettyTest {

    private static Server server;
    private static int port = 8083;
    private static UserService mockUserService;
    private static CatalogService mockCatalogService;
    private static ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void startServer() throws Exception {
        mockUserService = mock(UserService.class);
        mockCatalogService = mock(CatalogService.class);

        server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setAttribute("userService", mockUserService);
        context.setAttribute("catalogService", mockCatalogService);

        context.addServlet(new ServletHolder(new LoginServlet()), "/login");
        context.addServlet(new ServletHolder(new CatalogServlet()), "/catalogs");

        server.setHandler(context);
        server.start();
    }

    @AfterAll
    static void stopServer() throws Exception {
        server.stop();
    }

    @BeforeEach
    void resetMocks() {
        reset(mockUserService, mockCatalogService);

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
    void testDoGetCatalogs() throws Exception {
        Catalog catalog = new Catalog(1, "Catalog1");
        when(mockCatalogService.getAll()).thenReturn(List.of(catalog));

        String sessionCookie = loginAsAdmin();

        URL url = new URL("http://localhost:" + port + "/catalogs");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Cookie", sessionCookie);

        int code = connection.getResponseCode();
        assertEquals(200, code);

        try (InputStream is = connection.getInputStream()) {
            List<Map<String, Object>> responseList = mapper.readValue(is, List.class);
            assertEquals(1, responseList.size());
            assertEquals("Catalog1", responseList.get(0).get("name"));
        }
    }

    @Test
    void testDoPostCatalog() throws Exception {
        CatalogDTO dto = new CatalogDTO(0, "NewCatalog");
        Catalog entity = new Catalog("NewCatalog");

        when(mockCatalogService.createCatalog(any(Catalog.class))).thenReturn(true);

        String sessionCookie = loginAsAdmin();

        URL url = new URL("http://localhost:" + port + "/catalogs");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Cookie", sessionCookie);

        try (OutputStream os = connection.getOutputStream()) {
            mapper.writeValue(os, dto);
        }

        int code = connection.getResponseCode();
        assertEquals(201, code);

        try (InputStream is = connection.getInputStream()) {
            CatalogDTO response = mapper.readValue(is, CatalogDTO.class);
            assertEquals("NewCatalog", response.getName());
        }
    }

    @Test
    void testDoDeleteCatalog() throws Exception {
        when(mockCatalogService.deleteCatalog(1L)).thenReturn(true);

        String sessionCookie = loginAsAdmin();

        URL url = new URL("http://localhost:" + port + "/catalogs?id=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Cookie", sessionCookie);

        int code = connection.getResponseCode();
        assertEquals(200, code);
    }
}
