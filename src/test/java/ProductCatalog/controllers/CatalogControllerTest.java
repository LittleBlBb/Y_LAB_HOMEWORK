//package ProductCatalog.controllers;
//
//import ProductCatalog.dto.CatalogDTO;
//import ProductCatalog.models.Catalog;
//import ProductCatalog.services.implementations.CatalogService;
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
//public class CatalogControllerTest {
//
//    private CatalogService catalogService;
//    private MockMvc mockMvc;
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    @BeforeEach
//    void setUp() {
//        catalogService = Mockito.mock(CatalogService.class);
//        CatalogController controller = new CatalogController(catalogService);
//        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
//    }
//
//    @Test
//    void testGetCatalogs() throws Exception {
//        when(catalogService.getAll()).thenReturn(
//                List.of(new Catalog(1L, "Phones"))
//        );
//
//        mockMvc.perform(get("/api/catalogs"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].name").value("Phones"));
//    }
//
//    @Test
//    void testCreateCatalogSuccess() throws Exception {
//        CatalogDTO dto = new CatalogDTO(0L, "NewCatalog");
//
//        when(catalogService.createCatalog(any())).thenReturn(true);
//
//        mockMvc.perform(
//                        post("/api/catalogs")
//                                .contentType("application/json")
//                                .content(mapper.writeValueAsString(dto))
//                )
//                .andExpect(status().isOk())
//                .andExpect(content().string("CREATED"));
//    }
//
//    @Test
//    void testDeleteCatalog() throws Exception {
//        when(catalogService.deleteCatalog(1L)).thenReturn(true);
//
//        mockMvc.perform(delete("/api/catalogs?id=1"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("DELETED"));
//    }
//}
