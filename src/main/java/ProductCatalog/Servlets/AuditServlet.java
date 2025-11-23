package ProductCatalog.Servlets;

import ProductCatalog.DTO.AuditEntryDTO;
import ProductCatalog.DTO.ProductDTO;
import ProductCatalog.Mappers.AuditMapper;
import ProductCatalog.Mappers.ProductMapper;
import ProductCatalog.Models.AuditEntry;
import ProductCatalog.Models.Product;
import ProductCatalog.Services.AuditService;
import ProductCatalog.Validators.AuditValidator;
import ProductCatalog.Validators.ProductValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/audit")
public class AuditServlet extends HttpServlet {
    private AuditService auditService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public void init() throws ServletException {
        auditService = (AuditService) getServletContext().getAttribute("auditService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<AuditEntryDTO> dtoList = auditService.getAll()
                .stream().map(AuditMapper.INSTANCE::toDTO)
                .toList();

        resp.setContentType("application/json");
        mapper.writeValue(resp.getWriter(), dtoList);
    }
}
