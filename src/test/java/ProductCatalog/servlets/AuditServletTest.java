package ProductCatalog.servlets;

import ProductCatalog.dto.AuditEntryDTO;
import ProductCatalog.mappers.AuditMapper;
import ProductCatalog.models.AuditEntry;
import ProductCatalog.services.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuditServletTest {

    private AuditServlet servlet;
    private AuditService auditService;
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() throws Exception {
        auditService = mock(AuditService.class);
        mapper = new ObjectMapper();

        ServletContext context = mock(ServletContext.class);
        when(context.getAttribute("auditService")).thenReturn(auditService);

        servlet = new AuditServlet() {
            @Override
            public ServletContext getServletContext() {
                return context;
            }
        };

        servlet.init();
    }

    @Test
    public void testDoGet_returnsAuditList_asDTO() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getServletContext()).thenReturn(servlet.getServletContext());

        StringWriter writer = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(writer));

        List<AuditEntry> entries = List.of(
                new AuditEntry(1L, "user1", "CREATE", "Created catalog", LocalDateTime.of(2025, 11, 25, 10, 0)),
                new AuditEntry(2L, "user2", "DELETE", "Deleted product", LocalDateTime.of(2025, 11, 25, 11, 0))
        );

        when(auditService.getAll()).thenReturn(entries);

        servlet.doGet(req, resp);

        String json = writer.toString().trim();

        List<AuditEntryDTO> expectedList = entries.stream()
                .map(AuditMapper.INSTANCE::toDTO)
                .toList();

        List<AuditEntryDTO> actualList = mapper.readValue(
                json,
                mapper.getTypeFactory().constructCollectionType(List.class, AuditEntryDTO.class)
        );

        assertEquals(expectedList, actualList);

        verify(resp).setContentType("application/json");
    }
}
