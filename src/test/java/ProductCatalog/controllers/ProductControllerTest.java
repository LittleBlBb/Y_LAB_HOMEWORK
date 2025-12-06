//package ProductCatalog.controllers;
//
//import ProductCatalog.dto.ProductDTO;
//import ProductCatalog.models.Product;
//import ProductCatalog.services.implementations.ProductService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//public class ProductControllerTest {
//
//    private ProductService productService;
//    private MockMvc mockMvc;
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    @BeforeEach
//    void setUp() {
//        productService = Mockito.mock(ProductService.class);
//        ProductController controller = new ProductController(productService);
//        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
//    }
//
//    @Test
//    void testGetAllProducts() throws Exception {
//        when(productService.getAll())
//                .thenReturn(List.of(new Product(1L, 1L, "iPhone", 1000, "Apple", "Phones", "desc")));
//
//        mockMvc.perform(get("/api/products"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].name").value("iPhone"))
//                .andExpect(jsonPath("$[0].brand").value("Apple"));
//    }
//
//    @Test
//    void testGetProductsByCatalogId() throws Exception {
//        when(productService.getProducts(2L))
//                .thenReturn(List.of(new Product(2L, 2L, "Samsung", 500, "Samsung", "Phones", "desc")));
//
//        mockMvc.perform(get("/api/products?catalogId=2"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].name").value("Samsung"));
//    }
//
//    @Test
//    void testCreateProductSuccess() throws Exception {
//        ProductDTO dto = new ProductDTO(0L, 1L, "Test", 100, "Brand", "Cat", "desc");
//
//        when(productService.createProduct(any())).thenReturn(true);
//
//        mockMvc.perform(
//                        post("/api/products")
//                                .contentType("application/json")
//                                .content(mapper.writeValueAsString(dto))
//                )
//                .andExpect(status().isOk())
//                .andExpect(content().string("CREATED"));
//    }
//
//    @Test
//    void testDeleteProduct() throws Exception {
//        when(productService.deleteProduct(5L)).thenReturn(true);
//
//        mockMvc.perform(delete("/api/products?id=5"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("DELETED"));
//    }
//}
