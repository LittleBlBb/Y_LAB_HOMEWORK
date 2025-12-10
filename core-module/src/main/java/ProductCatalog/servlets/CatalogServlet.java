package ProductCatalog.servlets;

import ProductCatalog.annotations.Auditable;
import ProductCatalog.constants.Permission;
import ProductCatalog.dto.CatalogDTO;
import ProductCatalog.mappers.CatalogMapper;
import ProductCatalog.models.Catalog;
import ProductCatalog.services.implemetations.CatalogService;
import ProductCatalog.utils.AccessUtil;
import ProductCatalog.validators.CatalogValidator;
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

    @Auditable(action = "get all catalogs")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<CatalogDTO> dtoList = catalogService.getAll().stream()
                .map(CatalogMapper.INSTANCE::toDTO)
                        .toList();

        resp.setContentType("application/json");

        mapper.writeValue(resp.getWriter(), dtoList);
    }

    @Auditable(action = "create new catalog")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AccessUtil.checkPermission(req, Permission.CREATE_CATALOG);

        CatalogDTO dto = mapper.readValue(req.getInputStream(), CatalogDTO.class);

        List<String> errors = CatalogValidator.validate(dto);
        if (!errors.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            mapper.writeValue(resp.getWriter(),
                    Map.of("validation_errors", errors)
            );
            return;
        }

        Catalog entity = CatalogMapper.INSTANCE.toEntity(dto);

        boolean created = catalogService.createCatalog(entity);

        resp.setContentType("application/json");
        if(created) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(resp.getWriter(), CatalogMapper.INSTANCE.toDTO(entity));
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), "Catalog not created");
        }
    }

    @Auditable(action = "delete catalog")
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AccessUtil.checkPermission(req, Permission.DELETE_CATALOG);

        long id = Long.parseLong(req.getParameter("id"));
        boolean deleted = catalogService.deleteCatalog(id);

        if (deleted) {
            resp.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(resp.getWriter(), "Catalog deleted");
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mapper.writeValue(resp.getWriter(), "Catalog not found");
        }
    }
}
