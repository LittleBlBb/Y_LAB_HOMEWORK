package ProductCatalog.controllers;

import ProductCatalog.models.AuditEntry;
import ProductCatalog.services.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuditControllerTest {

    private AuditService auditService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        auditService = Mockito.mock(AuditService.class);
        AuditController controller = new AuditController(auditService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetAllLogs() throws Exception {
        when(auditService.getAll()).thenReturn(
                List.of(
                        new AuditEntry(1L, "user", "ACTION", "details", LocalDateTime.now())
                )
        );

        mockMvc.perform(get("/api/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user"))
                .andExpect(jsonPath("$[0].action").value("ACTION"));
    }
}
