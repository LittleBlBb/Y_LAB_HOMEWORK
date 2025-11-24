package ProductCatalog.Servlets;

import ProductCatalog.Annotations.Auditable;
import ProductCatalog.DTO.ProductDTO;
import ProductCatalog.Mappers.ProductMapper;
import ProductCatalog.Models.Product;
import ProductCatalog.Services.ProductService;
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

@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    private ProductService productService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public void init() throws ServletException {
        productService = (ProductService) getServletContext().getAttribute("productService");
    }

    @Override
    @Auditable
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
    @Auditable
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ProductDTO dto = mapper.readValue(req.getInputStream(), ProductDTO.class);

        List<String> errors = ProductValidator.validate(dto);
        if (!errors.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            mapper.writeValue(resp.getWriter(),
                    Map.of("validation_errors", errors)
            );
            return;
        }

        Product product = ProductMapper.INSTANCE.toEntity(dto);

        boolean created = productService.createProduct(product);

        if (created) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(resp.getWriter(), ProductMapper.INSTANCE.toDTO(product));
        }
        else{
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), "Product not created");
        }
    }

    @Override
    @Auditable
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = Long.parseLong(req.getParameter("id"));
        boolean deleted = productService.deleteProduct(id);
        resp.setStatus(deleted ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);
    }
}