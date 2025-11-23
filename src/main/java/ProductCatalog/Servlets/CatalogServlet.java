package ProductCatalog.Servlets;

import ProductCatalog.DTO.CatalogDTO;
import ProductCatalog.Mappers.CatalogMapper;
import ProductCatalog.Services.CatalogService;
import ProductCatalog.Validators.CatalogValidator;
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

@WebServlet("/catalogs")
public class CatalogServlet extends HttpServlet {
    private CatalogService catalogService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        this.catalogService = (CatalogService) getServletContext().getAttribute("catalogService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<CatalogDTO> dtoList = catalogService.getAll().stream()
                .map(CatalogMapper.INSTANCE::toDTO)
                        .toList();

        resp.setContentType("application/json");

        mapper.writeValue(resp.getWriter(), dtoList);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CatalogDTO dto = mapper.readValue(req.getInputStream(), CatalogDTO.class);

        List<String> errors = CatalogValidator.validate(dto);
        if (!errors.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            mapper.writeValue(resp.getWriter(),
                    Map.of("validation_errors", errors)
            );
            return;
        }

        boolean created = catalogService.createCatalog(
                CatalogMapper.INSTANCE.toEntity(dto)
        );
        resp.setStatus(created ? HttpServletResponse.SC_CREATED : HttpServletResponse.SC_BAD_REQUEST);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = Long.parseLong(req.getParameter("id"));
        boolean deleted = catalogService.deleteCatalog(id);
        resp.setStatus(deleted ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);
    }
}
