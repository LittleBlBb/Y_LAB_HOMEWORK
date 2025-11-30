package ProductCatalog.controllers;

import ProductCatalog.annotations.Auditable;
import ProductCatalog.validators.ProductValidator;
import ProductCatalog.dto.ProductDTO;
import ProductCatalog.mappers.ProductMapper;
import ProductCatalog.models.Product;
import ProductCatalog.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@Tag(name = "products", description = "operations with products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Auditable
    @GetMapping
    @Operation(summary = "getting all products or products by catalogId")
    public List<ProductDTO> getProducts(@RequestParam(name="catalogId", required = false) Long catalogId) {
        List<Product> products;

        if(catalogId != null) {
            products = productService.getProducts(catalogId);
        } else {
            products = productService.getAll();
        }

        return products.stream()
                .map(ProductMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Auditable
    @PostMapping
    @Operation(summary = "create new product")
    public String createProduct(@RequestBody ProductDTO productDTO) {
        List<String> errors = ProductValidator.validate(productDTO);
        if (!errors.isEmpty()) {
            return errors.stream().collect(Collectors.joining("\n"));
        }

        boolean created = productService.createProduct(ProductMapper.INSTANCE.toEntity(productDTO));

        if (created) {
            return "CREATED";
        } else {
            return "FAILED";
        }
    }

    @Auditable
    @DeleteMapping
    @Operation(summary = "delete product by id")
    public String deleteProduct(@RequestParam(name = "id", required = true) Long id) {
        boolean deleted = productService.deleteProduct(id);

        if (deleted) {
            return "DELETED";
        } else {
            return "FAILED";
        }
    }
}
