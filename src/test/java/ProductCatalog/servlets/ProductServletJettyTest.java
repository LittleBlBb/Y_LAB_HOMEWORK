package ProductCatalog.servlets;

import ProductCatalog.constants.Role;
import ProductCatalog.constants.Permission;
import ProductCatalog.dto.LoginRequest;
import ProductCatalog.dto.ProductDTO;
import ProductCatalog.models.Product;
import ProductCatalog.models.User;
import ProductCatalog.services.implemetations.ProductService;
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

class ProductServletJettyTest {

    private static Server server;
    private static int port = 8084;
    private static UserService mockUserService;
    private static ProductService mockProductService;
    private static ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void startServer() throws Exception {
        mockUserService = mock(UserService.class);
        mockProductService = mock(ProductService.class);

        server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setAttribute("userService", mockUserService);
        context.setAttribute("productService", mockProductService);

        context.addServlet(new ServletHolder(new LoginServlet()), "/login");
        context.addServlet(new ServletHolder(new ProductServlet()), "/products");

        server.setHandler(context);
        server.start();
    }

    @AfterAll
    static void stopServer() throws Exception {
        server.stop();
    }

    @BeforeEach
    void resetMocks() {
        reset(mockUserService, mockProductService);

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
    void testDoGetProducts() throws Exception {
        Product product = new Product(1, 1, "Product1", 100.0, "BrandA", "CategoryX", "Description1");
        when(mockProductService.getAll()).thenReturn(List.of(product));

        String sessionCookie = loginAsAdmin();

        URL url = new URL("http://localhost:" + port + "/products");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Cookie", sessionCookie);

        int code = connection.getResponseCode();
        assertEquals(200, code);

        try (InputStream is = connection.getInputStream()) {
            List<Map<String, Object>> responseList = mapper.readValue(is, List.class);
            assertEquals(1, responseList.size());
            assertEquals("Product1", responseList.get(0).get("name"));
        }
    }

    @Test
    void testDoPostProduct() throws Exception {
        ProductDTO dto = new ProductDTO(0L, 1L, "NewProduct", 150.0, "Desc", "BrandB", "CatY");
        Product entity = new Product(1L, "NewProduct", 150.0, "BrandB", "CatY", "Desc");

        when(mockProductService.createProduct(any(Product.class))).thenReturn(true);

        String sessionCookie = loginAsAdmin();

        URL url = new URL("http://localhost:" + port + "/products");
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
            ProductDTO response = mapper.readValue(is, ProductDTO.class);
            assertEquals("NewProduct", response.getName());
        }
    }

    @Test
    void testDoDeleteProduct() throws Exception {
        when(mockProductService.deleteProduct(1L)).thenReturn(true);

        String sessionCookie = loginAsAdmin();

        URL url = new URL("http://localhost:" + port + "/products?id=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Cookie", sessionCookie);

        int code = connection.getResponseCode();
        assertEquals(200, code);
    }
}
