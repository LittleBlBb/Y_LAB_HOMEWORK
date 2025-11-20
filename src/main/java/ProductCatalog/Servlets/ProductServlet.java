package ProductCatalog.Servlets;

import ProductCatalog.Models.Product;
import ProductCatalog.Services.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    private ProductService productService;

    @Override
    public void init() throws ServletException {
        productService = (ProductService) getServletContext().getAttribute("productService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String catalogIdStr = req.getParameter("catalogId");
        if(catalogIdStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "catalogId is required");
            return;
        }
        long catalogId = Long.parseLong(catalogIdStr);

        List<Product> products = productService.getProducts(catalogId);
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        out.println("[");
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            out.printf("{\"id\":%d,\"name\":\"%s\",\"price\":%.2f,\"brand\":\"%s\",\"category\":\"%s\"}%s%n",
                    product.getId(), product.getName(), product.getPrice(), product.getBrand(),  product.getCategory(),
                    i < products.size() ? "," : "");
        }
        out.println("]");
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String catalogIdStr = req.getParameter("catalogId");
        if(catalogIdStr == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "catalogId is required");
            return;
        }
        long catalogId = Long.parseLong(catalogIdStr);

        String name = req.getParameter("name");
        double price = Double.parseDouble(req.getParameter("price"));
        String brand = req.getParameter("brand");
        String category = req.getParameter("category");
        String description = req.getParameter("description");
        boolean created = productService.createProduct(
                new Product(
                        catalogId,
                        name,
                        price,
                        brand,
                        category,
                        description
                )
        );
        resp.setStatus(created ? HttpServletResponse.SC_CREATED : HttpServletResponse.SC_BAD_REQUEST);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = Long.parseLong(req.getParameter("id"));
        boolean deleted = productService.deleteProduct(id);
        resp.setStatus(deleted ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);
    }
}
