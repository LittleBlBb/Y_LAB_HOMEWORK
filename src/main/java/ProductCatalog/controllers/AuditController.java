package ProductCatalog.controllers;

import ProductCatalog.annotations.Auditable;
import ProductCatalog.services.AuditService;
import ProductCatalog.dto.AuditEntryDTO;
import ProductCatalog.mappers.AuditMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для управления Журналом аудита.
 * Предоставляет REST-эндпоинт для получения пользователей.
 * Использует {@link ProductCatalog.services.AuditService} для выполнения бизнес-логики и {@link ProductCatalog.mappers.AuditMapper}
 * для преобразования сущностей в DTO и обратно.
 *
 */
@RestController
@RequestMapping("/api/logs")
@Tag(name = "logs", description = "show all logs")
public class AuditController {
    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * Получение всех логов.
     * @return список логов в виде {@link ProductCatalog.dto.AuditEntryDTO}
     */
    @GetMapping
    @Operation(summary = "get all logs")
    public List<AuditEntryDTO> getAllLogs(){
        return auditService.getAll().stream()
                .map(AuditMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }
}
