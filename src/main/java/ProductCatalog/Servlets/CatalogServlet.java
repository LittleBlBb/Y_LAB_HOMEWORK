package ProductCatalog.Servlets;

import ProductCatalog.Models.Catalog;
import ProductCatalog.Services.CatalogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/catalogs")
public class CatalogServlet extends HttpServlet {
    private CatalogService catalogService;

    @Override
    public void init() throws ServletException {
        this.catalogService = (CatalogService) getServletContext().getAttribute("catalogService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Catalog> catalogs =  catalogService.getAllCatalogs();
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        out.println("[");
        for (int i = 0; i < catalogs.size(); i++) {
            Catalog catalog = catalogs.get(i);
            out.printf("{\"id\":%d,\"name\":\"%s\"}%s%n",
                    catalog.getId(), catalog.getName(),
                    i > catalogs.size() - 1 ? "," : "");
        }
        out.println("]");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        boolean created = catalogService.createCatalog(new Catalog(name));
        resp.setStatus(created ? HttpServletResponse.SC_CREATED : HttpServletResponse.SC_BAD_REQUEST);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = Long.parseLong(req.getParameter("id"));
        boolean deleted = catalogService.deleteCatalog(id);
        resp.setStatus(deleted ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);
    }
}
