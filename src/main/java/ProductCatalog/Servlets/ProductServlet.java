package ProductCatalog.Servlets;

import ProductCatalog.DTO.ProductDTO;
import ProductCatalog.Mappers.ProductMapper;
import ProductCatalog.Models.Product;
import ProductCatalog.Services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    private ProductService productService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public void init() throws ServletException {
        productService = (ProductService) getServletContext().getAttribute("productService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String catalogIdStr = req.getParameter("catalogId");

        List<Product> products =
                (catalogIdStr == null)
                        ? productService.getAll()
                        : productService.getProducts(Long.parseLong(catalogIdStr));

        List<ProductDTO> dtoList = products.stream()
                .map(ProductMapper.INSTANCE::toDTO)
                .toList();

        resp.setContentType("application/json");
        mapper.writeValue(resp.getWriter(), dtoList);
    }



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ProductDTO dto = mapper.readValue(req.getInputStream(), ProductDTO.class);

        Product product = ProductMapper.INSTANCE.toEntity(dto);

        boolean created = productService.createProduct(product);

        resp.setStatus(created ? HttpServletResponse.SC_OK :  HttpServletResponse.SC_BAD_REQUEST);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = Long.parseLong(req.getParameter("id"));
        boolean deleted = productService.deleteProduct(id);
        resp.setStatus(deleted ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);
    }
}