package ProductCatalog.controllers;

import ProductCatalog.annotations.Auditable;
import ProductCatalog.constants.Permission;
import ProductCatalog.mappers.ProductMapper;
import ProductCatalog.models.Catalog;
import ProductCatalog.repositories.implemetations.CatalogRepository;
import ProductCatalog.services.implementations.CatalogService;
import ProductCatalog.utils.AccessUtil;
import ProductCatalog.validators.CatalogValidator;
import ProductCatalog.dto.CatalogDTO;
import ProductCatalog.mappers.CatalogMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/catalogs")
@Api(tags = "catalogs")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    @ApiOperation("getting all catalogs or catalog by catalogId")
    public List<CatalogDTO> getCatalogs() {
        return catalogService.getAll().stream()
                .map(CatalogMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Auditable(action = "create new catalog")
    @PostMapping
    @ApiOperation("create new catalog")
    public ResponseEntity<?> createCatalog(@RequestBody CatalogDTO catalogDTO,
                                           HttpServletRequest request) throws AccessDeniedException {

        AccessUtil.checkPermission(request, Permission.CREATE_CATALOG);

        List<String> errors = CatalogValidator.validate(catalogDTO);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Catalog entity = CatalogMapper.INSTANCE.toEntity(catalogDTO);
        Catalog created = catalogService.createCatalog(entity);

        if (created == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating product");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CatalogMapper.INSTANCE.toDTO(created));
    }

    @Auditable(action = "delete catalog")
    @DeleteMapping("/{id}")
    @ApiOperation("delete catalog by id")
    public ResponseEntity<Void> deleteCatalogById(@PathVariable Long id,
                                                  HttpServletRequest request) throws AccessDeniedException {

        AccessUtil.checkPermission(request, Permission.DELETE_CATALOG);

        boolean deleted = catalogService.deleteCatalog(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
