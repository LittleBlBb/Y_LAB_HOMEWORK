package ProductCatalog.servlets;

import ProductCatalog.annotations.Auditable;
import ProductCatalog.dto.ProductDTO;
import ProductCatalog.mappers.ProductMapper;
import ProductCatalog.models.Product;
import ProductCatalog.models.Role;
import ProductCatalog.models.User;
import ProductCatalog.services.implemetations.ProductService;
import ProductCatalog.validators.ProductValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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

    @Auditable(action = "get all products")
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

    @Auditable(action = "create new product")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        HttpSession session = req.getSession();
        Object user = session.getAttribute(SessionAttributes.USER);
        if (!((User) user).getRole().equals(Role.ADMIN)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            mapper.writeValue(resp.getWriter(), "You are not allowed to perform this action");
        }

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
        else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), "Product not created");
        }
    }

    @Auditable(action = "delete product")
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = Long.parseLong(req.getParameter("id"));
        HttpSession session = req.getSession();
        Object user = session.getAttribute(SessionAttributes.USER);
        if (!((User) user).getRole().equals(Role.ADMIN)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            mapper.writeValue(resp.getWriter(), "You are not allowed to perform this action");
        }
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            resp.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(resp.getWriter(), "Product deleted");
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mapper.writeValue(resp.getWriter(), "Product not found");
        }
    }
}