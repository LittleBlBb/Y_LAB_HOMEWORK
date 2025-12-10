package ProductCatalog.controllers;

import ProductCatalog.constants.Permission;
import ProductCatalog.constants.Role;
import ProductCatalog.dto.ProductDTO;
import ProductCatalog.models.Product;
import ProductCatalog.models.User;
import ProductCatalog.services.implementations.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.file.AccessDeniedException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductControllerTest extends BaseControllerTest {

    private ProductService productService;
    private ProductController productController;

    @BeforeEach
    void setUp() {
        super.setUpBase();
        productService = mock(ProductService.class);
        productController = new ProductController(productService);
    }

    @Test
    void getProducts_returnsMappedDTOs() {
        Product p1 = new Product(1L, 1L, "Product1", 10.0, "Brand1", "Category1", "Desc1");
        Product p2 = new Product(2L, 1L, "Product2", 20.0, "Brand2", "Category2", "Desc2");

        when(productService.getAll()).thenReturn(List.of(p1, p2));

        var result = productController.getProducts(null);

        assertEquals(2, result.size());
        assertEquals("Product1", result.get(0).getName());
        assertEquals("Product2", result.get(1).getName());
        verify(productService, times(1)).getAll();
    }

    @Test
    void getProductsByCatalog_returnsMappedDTOs() {
        Long catalogId = 1L;
        Product p1 = new Product(1L, catalogId, "Product1", 10.0, "Brand1", "Category1", "Desc1");

        when(productService.getProducts(catalogId)).thenReturn(List.of(p1));

        var result = productController.getProducts(catalogId);

        assertEquals(1, result.size());
        assertEquals("Product1", result.get(0).getName());
        verify(productService, times(1)).getProducts(catalogId);
    }

    @Test
    void createProduct_returnsCreatedDTO_whenUserHasPermission() throws Exception {
        ProductDTO dto = new ProductDTO(0, 1L, "NewProduct", 10.0, "Desc", "Brand", "Category");

        Product saved = new Product(1L, 1L, "NewProduct", 10.0, "Brand", "Category", "Desc");
        when(productService.createProduct(any(Product.class))).thenReturn(saved);

        ResponseEntity result = productController.createProduct(dto, request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void createProduct_returns500_whenServiceReturnsNull() throws Exception {
        ProductDTO dto = new ProductDTO(0, 1L, "NewProduct", 10.0, "Desc", "Brand", "Category");

        when(productService.createProduct(any(Product.class))).thenReturn(null);

        ResponseEntity result = productController.createProduct(dto, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Error creating product", result.getBody());
    }

    @Test
    void createProduct_throwsAccessDenied_whenNotLoggedIn() {
        ProductDTO dto = new ProductDTO(0, 1L, "NewProduct", 10.0, "Desc", "Brand", "Category");

        assertThrows(AccessDeniedException.class, () ->
                productController.createProduct(dto, createNotLoggedInRequest())
        );
    }

    @Test
    void createProduct_throwsAccessDenied_whenNoPermission() {
        User normalUser = new User(2L, "user", "pass", Role.USER);
        ProductDTO dto = new ProductDTO(0, 1L, "NewProduct", 10.0, "Desc", "Brand", "Category");

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
                productController.createProduct(dto, createUserRequest(normalUser))
        );

        assertEquals("You do not have permission: " + Permission.CREATE_PRODUCT, ex.getMessage());
    }

    @Test
    void updateProduct_returnsUpdatedDTO_whenUserHasPermission() throws Exception {
        ProductDTO dto = new ProductDTO(1L, 1L, "UpdatedProduct", 15.0, "Desc", "Brand", "Category");

        when(productService.updateProduct(any(Product.class))).thenReturn(true);

        ResponseEntity<?> result = productController.updateProduct(dto, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        verify(productService).updateProduct(any(Product.class));
    }

    @Test
    void updateProduct_returns404_whenUpdateFailed() throws Exception {
        ProductDTO dto = new ProductDTO(1L, 1L, "UpdatedProduct", 15.0, "Desc", "Brand", "Category");

        when(productService.updateProduct(any(Product.class))).thenReturn(false);

        ResponseEntity<?> result = productController.updateProduct(dto, request);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void updateProduct_throwsAccessDenied_whenNotLoggedIn() {
        ProductDTO dto = new ProductDTO(1L, 1L, "UpdatedProduct", 15.0, "Desc", "Brand", "Category");

        assertThrows(AccessDeniedException.class, () ->
                productController.updateProduct(dto, createNotLoggedInRequest())
        );
    }

    @Test
    void updateProduct_throwsAccessDenied_whenNoPermission() {
        User normalUser = new User(2L, "user", "pass", Role.USER);
        ProductDTO dto = new ProductDTO(1L, 1L, "UpdatedProduct", 15.0, "Desc", "Brand", "Category");

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
                productController.updateProduct(dto, createUserRequest(normalUser))
        );

        assertEquals("You do not have permission: " + Permission.EDIT_PRODUCT, ex.getMessage());
    }

    @Test
    void deleteProduct_returns204_whenDeleted() throws Exception {
        Long id = 1L;

        when(productService.deleteProduct(id)).thenReturn(true);

        ResponseEntity<Void> response = productController.deleteProduct(id, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(productService).deleteProduct(id);
    }

    @Test
    void deleteProduct_returns404_whenNotDeleted() throws Exception {
        Long id = 1L;

        when(productService.deleteProduct(id)).thenReturn(false);

        ResponseEntity<Void> response = productController.deleteProduct(id, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteProduct_throwsAccessDenied_whenNotLoggedIn() {
        Long id = 1L;

        assertThrows(AccessDeniedException.class, () ->
                productController.deleteProduct(id, createNotLoggedInRequest())
        );
    }

    @Test
    void deleteProduct_throwsAccessDenied_whenNoPermission() {
        User normalUser = new User(2L, "user", "pass", Role.USER);
        Long id = 1L;

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
                productController.deleteProduct(id, createUserRequest(normalUser))
        );

        assertEquals("You do not have permission: " + Permission.DELETE_PRODUCT, ex.getMessage());
    }
}