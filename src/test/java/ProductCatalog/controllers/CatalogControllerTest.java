package ProductCatalog.controllers;

import ProductCatalog.constants.Permission;
import ProductCatalog.constants.Role;
import ProductCatalog.models.Catalog;
import ProductCatalog.models.User;
import ProductCatalog.services.implementations.CatalogService;
import ProductCatalog.dto.CatalogDTO;
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

class CatalogControllerTest extends BaseControllerTest {

    private CatalogService catalogService;
    private CatalogController catalogController;

    @BeforeEach
    void setUp() {
        super.setUpBase();
        catalogService = mock(CatalogService.class);
        catalogController = new CatalogController(catalogService);
    }

    @Test
    void getCatalogs_returnsMappedDTOs() {
        Catalog cat1 = new Catalog(1L, "Cat1");
        Catalog cat2 = new Catalog(2L, "Cat2");

        when(catalogService.getAll()).thenReturn(List.of(cat1, cat2));

        var result = catalogController.getCatalogs();

        assertEquals(2, result.size());
        assertEquals("Cat1", result.get(0).getName());
        assertEquals("Cat2", result.get(1).getName());

        verify(catalogService, times(1)).getAll();
    }

    @Test
    void createCatalog_returnsCreated_whenUserHasPermission() throws Exception {
        CatalogDTO dto = new CatalogDTO(0, "NewCatalog");
        when(catalogService.createCatalog(any(Catalog.class))).thenReturn(true);

        String result = catalogController.createCatalog(dto, request);

        assertEquals("CREATED", result);
        verify(catalogService, times(1)).createCatalog(any(Catalog.class));
    }

    @Test
    void createCatalog_throwsAccessDeniedException_whenUserNotLoggedIn() {
        CatalogDTO dto = new CatalogDTO(0, "NewCatalog");
        assertThrows(AccessDeniedException.class, () -> {
            catalogController.createCatalog(dto, createNotLoggedInRequest());
        });
    }

    @Test
    void createCatalog_throwsAccessDeniedException_whenUserHasNoPermission() {
        User normalUser = new User(2L, "user", "pass", Role.USER);
        CatalogDTO dto = new CatalogDTO(0, "NewCatalog");

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            catalogController.createCatalog(dto, createUserRequest(normalUser));
        });

        assertEquals("You do not have permission: " + Permission.CREATE_CATALOG, ex.getMessage());
    }


    @Test
    void deleteCatalogById_returnsDeleted_whenUserHasPermission() throws Exception {
        Long catalogId = 1L;
        when(catalogService.deleteCatalog(catalogId)).thenReturn(true);

        String result = catalogController.deleteCatalogById(catalogId, request);

        assertEquals("DELETED", result);
        verify(catalogService, times(1)).deleteCatalog(catalogId);
    }

    @Test
    void deleteCatalogById_throwsAccessDeniedException_whenUserNotLoggedIn() {
        Long catalogId = 1L;
        assertThrows(AccessDeniedException.class, () -> {
            catalogController.deleteCatalogById(catalogId, createNotLoggedInRequest());
        });
    }

    @Test
    void deleteCatalogById_throwsAccessDeniedException_whenUserHasNoPermission() {
        User normalUser = new User(2L, "user", "pass", Role.USER);
        Long catalogId = 1L;

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            catalogController.deleteCatalogById(catalogId, createUserRequest(normalUser));
        });

        assertEquals("You do not have permission: " + Permission.DELETE_CATALOG, ex.getMessage());
    }
}
