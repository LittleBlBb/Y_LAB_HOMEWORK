package ProductCatalog.controllers;

import ProductCatalog.constants.Permission;
import ProductCatalog.constants.Role;
import ProductCatalog.models.Product;
import ProductCatalog.models.User;
import ProductCatalog.services.implementations.ProductService;
import ProductCatalog.dto.ProductDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.AccessDeniedException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void createProduct_returnsCreated_whenUserHasPermission() throws Exception {
        ProductDTO dto = new ProductDTO(0, 1L, "NewProduct", 10.0, "Desc", "Brand", "Category");
        when(productService.createProduct(any(Product.class))).thenReturn(true);

        String result = productController.createProduct(dto, request);

        assertEquals("CREATED", result);
        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void createProduct_throwsAccessDeniedException_whenUserNotLoggedIn() {
        ProductDTO dto = new ProductDTO(0, 1L, "NewProduct", 10.0, "Desc", "Brand", "Category");
        assertThrows(AccessDeniedException.class, () -> {
            productController.createProduct(dto, createNotLoggedInRequest());
        });
    }

    @Test
    void createProduct_throwsAccessDeniedException_whenUserHasNoPermission() {
        User normalUser = new User(2L, "user", "pass", Role.USER);
        ProductDTO dto = new ProductDTO(0, 1L, "NewProduct", 10.0, "Desc", "Brand", "Category");

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            productController.createProduct(dto, createUserRequest(normalUser));
        });

        assertEquals("You do not have permission: " + Permission.CREATE_PRODUCT, ex.getMessage());
    }

    @Test
    void updateProduct_returnsUpdated_whenUserHasPermission() throws Exception {
        ProductDTO dto = new ProductDTO(1L, 1L, "UpdatedProduct", 15.0, "Desc", "Brand", "Category");
        when(productService.updateProduct(any(Product.class))).thenReturn(true);

        String result = productController.updateProduct(dto, request);

        assertEquals("UPDATED", result);
        verify(productService, times(1)).updateProduct(any(Product.class));
    }

    @Test
    void updateProduct_throwsAccessDeniedException_whenUserNotLoggedIn() {
        ProductDTO dto = new ProductDTO(1L, 1L, "UpdatedProduct", 15.0, "Desc", "Brand", "Category");
        assertThrows(AccessDeniedException.class, () -> {
            productController.updateProduct(dto, createNotLoggedInRequest());
        });
    }

    @Test
    void updateProduct_throwsAccessDeniedException_whenUserHasNoPermission() {
        User normalUser = new User(2L, "user", "pass", Role.USER);
        ProductDTO dto = new ProductDTO(1L, 1L, "UpdatedProduct", 15.0, "Desc", "Brand", "Category");

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            productController.updateProduct(dto, createUserRequest(normalUser));
        });

        assertEquals("You do not have permission: " + Permission.EDIT_PRODUCT, ex.getMessage());
    }

    @Test
    void deleteProduct_returnsDeleted_whenUserHasPermission() throws Exception {
        Long productId = 1L;
        when(productService.deleteProduct(productId)).thenReturn(true);

        String result = productController.deleteProduct(productId, request);

        assertEquals("DELETED", result);
        verify(productService, times(1)).deleteProduct(productId);
    }

    @Test
    void deleteProduct_throwsAccessDeniedException_whenUserNotLoggedIn() {
        Long productId = 1L;
        assertThrows(AccessDeniedException.class, () -> {
            productController.deleteProduct(productId, createNotLoggedInRequest());
        });
    }

    @Test
    void deleteProduct_throwsAccessDeniedException_whenUserHasNoPermission() {
        User normalUser = new User(2L, "user", "pass", Role.USER);
        Long productId = 1L;

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            productController.deleteProduct(productId, createUserRequest(normalUser));
        });

        assertEquals("You do not have permission: " + Permission.DELETE_PRODUCT, ex.getMessage());
    }
}
