package ProductCatalog.controllers;

import ProductCatalog.constants.Permission;
import ProductCatalog.constants.Role;
import ProductCatalog.models.User;
import ProductCatalog.dto.AuditEntryDTO;
import ProductCatalog.models.AuditEntry;
import ProductCatalog.services.implementations.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuditControllerTest extends BaseControllerTest {

    private AuditService auditService;
    private AuditController auditController;

    @BeforeEach
    void setUp() {
        super.setUpBase();
        auditService = mock(AuditService.class);
        auditController = new AuditController(auditService);
    }

    @Test
    void getAllLogs_returnsMappedDTOs() throws Exception {
        AuditEntry entry1 = new AuditEntry(1L, "user1", "CREATE", "Created something", LocalDateTime.now());
        AuditEntry entry2 = new AuditEntry(2L, "user2", "EDIT", "Edited something", LocalDateTime.now());

        when(auditService.getAll()).thenReturn(List.of(entry1, entry2));

        List<AuditEntryDTO> result = auditController.getAllLogs(request);

        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("CREATE", result.get(0).getAction());
        assertEquals("user2", result.get(1).getUsername());
        assertEquals("EDIT", result.get(1).getAction());

        verify(auditService, times(1)).getAll();
    }

    @Test
    void getAllLogs_throwsAccessDeniedException_whenUserNotLoggedIn() {
        assertThrows(AccessDeniedException.class, () -> {
            auditController.getAllLogs(createNotLoggedInRequest());
        });
    }

    @Test
    void getAllLogs_throwsAccessDeniedException_whenUserHasNoPermission() {
        User normalUser = new User(2L, "user", "pass", Role.USER);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            auditController.getAllLogs(createUserRequest(normalUser));
        });

        assertEquals("You do not have permission: " + Permission.VIEW_AUDIT, exception.getMessage());
    }
}
