package ProductCatalog.Servlets;

import ProductCatalog.DTO.CatalogDTO;
import ProductCatalog.Models.Catalog;
import ProductCatalog.Services.CatalogService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CatalogServletTest {

    private CatalogServlet servlet;
    private CatalogService catalogService;
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() throws Exception {
        catalogService = mock(CatalogService.class);
        mapper = new ObjectMapper();

        ServletContext context = mock(ServletContext.class);
        when(context.getAttribute("catalogService")).thenReturn(catalogService);

        servlet = new CatalogServlet() {
            @Override
            public ServletContext getServletContext() {
                return context;
            }
        };
        servlet.init();
    }

    private ServletInputStream createStream(String json) {
        return new ServletInputStream() {
            private int index = 0;
            private final byte[] data = json.getBytes();

            @Override
            public int read() {
                return index < data.length ? data[index++] : -1;
            }

            @Override public boolean isFinished() { return index >= data.length; }
            @Override public boolean isReady() { return true; }
            @Override public void setReadListener(jakarta.servlet.ReadListener readListener) {}
        };
    }

    @Test
    public void testDoGet_returnsDTOList() throws Exception {

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(req.getServletContext()).thenReturn(servlet.getServletContext());

        StringWriter writer = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(writer));

        List<Catalog> data = List.of(
                new Catalog(1L, "Electronics"),
                new Catalog(2L, "Books")
        );

        when(catalogService.getAll()).thenReturn(data);

        servlet.doGet(req, resp);

        List<CatalogDTO> actual =
                mapper.readValue(writer.toString(), new TypeReference<>() {});

        List<CatalogDTO> expected = List.of(
                new CatalogDTO(1L, "Electronics"),
                new CatalogDTO(2L, "Books")
        );

        assertEquals(expected, actual);
        verify(resp).setContentType("application/json");
    }

    @Test
    public void testDoPost_createsCatalog_success() throws Exception {

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(req.getServletContext()).thenReturn(servlet.getServletContext());

        CatalogDTO dto = new CatalogDTO();
        dto.setName("NewCatalog");
        String json = mapper.writeValueAsString(dto);

        when(req.getInputStream()).thenReturn(createStream(json));

        StringWriter writer = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(writer));

        when(catalogService.createCatalog(any())).thenReturn(true);

        servlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_CREATED);

        CatalogDTO returnedDto =
                mapper.readValue(writer.toString(), CatalogDTO.class);

        assertEquals(0, returnedDto.getId());
        assertEquals("NewCatalog", returnedDto.getName());

        verify(resp).setContentType("application/json");
    }

    @Test
    public void testDoPost_validationError() throws Exception {

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(req.getServletContext()).thenReturn(servlet.getServletContext());

        String json = "{\"name\":\"\"}";
        when(req.getInputStream()).thenReturn(createStream(json));

        StringWriter writer = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(writer));

        servlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        Map<String, Object> body =
                mapper.readValue(writer.toString(), new TypeReference<>() {});

        assertEquals(true, body.containsKey("validation_errors"));
    }

    @Test
    public void testDoDelete_success() throws Exception {

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(req.getServletContext()).thenReturn(servlet.getServletContext());
        when(req.getParameter("id")).thenReturn("5");

        when(catalogService.deleteCatalog(5L)).thenReturn(true);

        StringWriter writer = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(writer));

        servlet.doDelete(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_OK);

        assertEquals("\"Catalog deleted\"", writer.toString().trim());
    }
}
