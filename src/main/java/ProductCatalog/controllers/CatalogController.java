package ProductCatalog.controllers;

import ProductCatalog.annotations.Auditable;
import ProductCatalog.constants.Permission;
import ProductCatalog.models.Catalog;
import ProductCatalog.services.implementations.CatalogService;
import ProductCatalog.utils.AccessUtil;
import ProductCatalog.validators.CatalogValidator;
import ProductCatalog.dto.CatalogDTO;
import ProductCatalog.mappers.CatalogMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для управления каталогами.
 * Предоставляет REST-эндпоинты для получения, создания и удаления каталогов.
 * Использует {@link ProductCatalog.services.implementations.CatalogService} для выполнения бизнес-логики и {@link ProductCatalog.mappers.CatalogMapper}
 * для преобразования сущностей в DTO и обратно.
 *
 * Аннотация {@link Auditable} применяется для аудита действий.
 */
@RestController
@RequestMapping("/api/catalogs")
@Api(tags = "catalogs")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    /**
     * Получение списка каталогов.
     * @return список каталогов в виде {@link ProductCatalog.dto.CatalogDTO}.
     */
    @GetMapping
    @ApiOperation("getting all catalogs or catalog by catalogId")
    public List<CatalogDTO> getCatalogs() {
        return catalogService.getAll().stream()
                .map(CatalogMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Создание нового каталога.
     * Перед созданием происходит валидация входных данных при помощи {@link CatalogValidator}.
     * В случае ошибок возвращается строка с перечнем ошибок.
     * Если создание успешно - возвращается {@code "CREATED"}.
     * Иначе - возвращается {@code "FAILED"}.
     * @param catalogDTO - DTO каталога, который необходимо создать.
     * @return строковый статус выполнения операции.
     */
    @Auditable(action = "create new catalog")
    @PostMapping
    @ApiOperation("create new catalog")
    public String createCatalog(@RequestBody CatalogDTO catalogDTO, HttpServletRequest request) throws AccessDeniedException {
        AccessUtil.checkPermission(request, Permission.CREATE_CATALOG);
        List<String> errors = CatalogValidator.validate(catalogDTO);
        if (!errors.isEmpty()) {
            return errors.stream().collect(Collectors.joining("\n"));
        }

        Catalog entity = CatalogMapper.INSTANCE.toEntity(catalogDTO);

        boolean created = catalogService.createCatalog(entity);

        if(created) {
            return "CREATED";
        } else {
            return "FAILED";
        }
    }

    /**
     * Удаление каталога.
     * Если каталог удален успешно - возвращается {@code "DELETED"}
     * Иначе - возвращается {@code "FAILED"}
     * @param id - id каталога, который необходимо удалить
     * @return - строковый статус выполнения операции
     */
    @Auditable(action = "delete catalog")
    @DeleteMapping
    @ApiOperation("delete catalog by id")
    public String deleteCatalogById(@RequestParam(name = "id", required = true) Long id, HttpServletRequest request) throws AccessDeniedException {
        AccessUtil.checkPermission(request, Permission.DELETE_CATALOG);
        boolean deleted = catalogService.deleteCatalog(id);
        if(deleted) {
            return "DELETED";
        } else {
            return "FAILED";
        }
    }
}
