package ProductCatalog.controllers;

import ProductCatalog.models.Catalog;
import ProductCatalog.services.CatalogService;
import ProductCatalog.validators.CatalogValidator;
import ProductCatalog.dto.CatalogDTO;
import ProductCatalog.mappers.CatalogMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/catalogs")
@Tag(name = "catalogs", description = "operations with catalogs")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    @Operation(summary = "getting all catalogs or catalog by catalogId")
    public List<CatalogDTO> getCatalogs() {
        return catalogService.getAll().stream()
                .map(CatalogMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    @Operation(summary = "create new catalog")
    public String createCatalog(@RequestBody CatalogDTO catalogDTO) {
        List<String> errors = CatalogValidator.validate(catalogDTO);
        if (!errors.isEmpty()) {
            return errors.stream().collect(Collectors.joining("\n"));
        }

        Catalog entity = CatalogMapper.INSTANCE.toEntity(catalogDTO);

        boolean created = catalogService.createCatalog(entity);

        if(created) {
            return "Catalog created";
        } else {
            return "Catalog could not be created";
        }
    }

    @DeleteMapping
    @Operation(summary = "delete catalog by id")
    public String deleteCatalogById(@RequestParam(name = "id", required = true) Long id) {
        boolean deleted = catalogService.deleteCatalog(id);
        if(deleted) {
            return "Catalog deleted";
        } else {
            return "Catalog could not be deleted";
        }
    }
}
