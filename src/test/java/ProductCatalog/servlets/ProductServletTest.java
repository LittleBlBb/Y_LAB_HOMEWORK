package ProductCatalog.servlets;

import ProductCatalog.dto.ProductDTO;
import ProductCatalog.mappers.ProductMapper;
import ProductCatalog.models.Product;
import ProductCatalog.models.Role;
import ProductCatalog.models.User;
import ProductCatalog.services.implemetations.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductServletTest {

    private ProductServlet servlet;
    private ProductService productService;
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() throws Exception {
        productService = mock(ProductService.class);
        mapper = new ObjectMapper();

        ServletContext context = mock(ServletContext.class);
        when(context.getAttribute("productService")).thenReturn(productService);

        servlet = new ProductServlet() {
            @Override
            public ServletContext getServletContext() {
                return context;
            }
        };

        servlet.init();
    }

    private ServletInputStream createInputStream(String json) {
        return new ServletInputStream() {
            private int pos = 0;
            private final byte[] data = json.getBytes();

            @Override
            public int read() {
                return pos < data.length ? data[pos++] : -1;
            }

            @Override public boolean isFinished() { return pos >= data.length; }
            @Override public boolean isReady() { return true; }
            @Override public void setReadListener(jakarta.servlet.ReadListener readListener) {}
        };
    }

    @Test
    public void testDoGet_returnsProductList_asDTO() throws Exception {

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(req.getServletContext()).thenReturn(servlet.getServletContext());

        StringWriter writer = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(writer));

        List<Product> products = List.of(
                new Product(1L, 1L, "Phone", 500.0, "BrandA", "Electronics", "Good phone"),
                new Product(2L, 1L, "Laptop", 1200.0, "BrandB", "Electronics", "Powerful laptop")
        );

        when(productService.getAll()).thenReturn(products);

        servlet.doGet(req, resp);

        List<ProductDTO> expected = products.stream()
                .map(ProductMapper.INSTANCE::toDTO)
                .toList();

        List<ProductDTO> actual = mapper.readValue(
                writer.toString(),
                mapper.getTypeFactory().constructCollectionType(List.class, ProductDTO.class)
        );

        assertEquals(expected, actual);
        verify(resp).setContentType("application/json");
    }

    @Test
    public void testDoPost_createsProduct_success() throws Exception {

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(req.getServletContext()).thenReturn(servlet.getServletContext());
        when(req.getSession()).thenReturn(session);

        User admin = new User();
        admin.setRole(Role.ADMIN);

        when(session.getAttribute(SessionAttributes.USER)).thenReturn(admin);

        ProductDTO dto = new ProductDTO(
                0, 1L, "Phone", 500.0, "Good phone", "BrandA", "Electronics"
        );

        String json = mapper.writeValueAsString(dto);
        when(req.getInputStream()).thenReturn(createInputStream(json));

        StringWriter writer = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(writer));

        when(productService.createProduct(any())).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(1L);
            return true;
        });

        servlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_CREATED);

        ProductDTO actual =
                mapper.readValue(writer.toString(), ProductDTO.class);

        ProductDTO expected = ProductMapper.INSTANCE
                .toDTO(new Product(
                        1L, 1L, "Phone", 500.0, "BrandA", "Electronics", "Good phone"
                ));

        assertEquals(expected, actual);
        verify(resp).setContentType("application/json");
    }

    @Test
    public void testDoPost_validationError() throws Exception {

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(req.getServletContext()).thenReturn(servlet.getServletContext());
        when(req.getSession()).thenReturn(session);

        User admin = new User();
        admin.setRole(Role.ADMIN);

        when(session.getAttribute(SessionAttributes.USER)).thenReturn(admin);


        String json = """
                {"catalogId":1, "name":"", "price":500}
                """;

        when(req.getInputStream()).thenReturn(createInputStream(json));

        StringWriter writer = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(writer));

        servlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        Map<String, Object> body =
                mapper.readValue(writer.toString(), Map.class);

        assertEquals(true, body.containsKey("validation_errors"));
    }

    @Test
    public void testDoDelete_success() throws Exception {

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(req.getServletContext()).thenReturn(servlet.getServletContext());
        when(req.getSession()).thenReturn(session);

        User admin = new User();
        admin.setRole(Role.ADMIN);
        when(session.getAttribute(SessionAttributes.USER)).thenReturn(admin);

        when(req.getParameter("id")).thenReturn("5");
        when(resp.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        when(productService.deleteProduct(5L)).thenReturn(true);

        servlet.doDelete(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);
    }
}
