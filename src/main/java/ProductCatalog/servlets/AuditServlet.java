package ProductCatalog.servlets;

import ProductCatalog.annotations.Auditable;
import ProductCatalog.dto.AuditEntryDTO;
import ProductCatalog.mappers.AuditMapper;
import ProductCatalog.services.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/logs")
public class AuditServlet extends HttpServlet {
    private AuditService auditService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public void init() throws ServletException {
        auditService = (AuditService) getServletContext().getAttribute("auditService");
    }

    @Auditable(action = "get all logs")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<AuditEntryDTO> dtoList = auditService.getAll()
                .stream().map(AuditMapper.INSTANCE::toDTO)
                .toList();

        resp.setContentType("application/json");
        mapper.writeValue(resp.getWriter(), dtoList);
    }
}
