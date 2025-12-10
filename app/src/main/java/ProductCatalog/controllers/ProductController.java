package ProductCatalog.controllers;

import ProductCatalog.audit.annotations.Auditable;
import ProductCatalog.constants.Permission;
import ProductCatalog.utils.AccessUtil;
import ProductCatalog.validators.ProductValidator;
import ProductCatalog.dto.ProductDTO;
import ProductCatalog.mappers.ProductMapper;
import ProductCatalog.models.Product;
import ProductCatalog.services.implementations.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для управления товарами в каталоге.
 * Предоставляет REST-эндпоинты для получения, создания, обновления и удаления товаров.
 * Использует {@link ProductService} для выполнения бизнес-логики и {@link ProductMapper}
 * для преобразования сущностей в DTO и обратно.
 *
 * Аннотация {@link Auditable} применяется для аудита действий.
 */
@RestController
@RequestMapping("/api/products")
@Tag(name = "products", description = "operations with proucts")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Получение списка товаров.
     * Если указан параметр {@code catalogId}, будут возвращены товары только из этого каталога.
     * Если параметр не указан - возвращаются все товары.
     * @param catalogId - необязательный параметр ID каталога
     * @return список товаров в виде {@link ProductDTO}
     */
    @GetMapping
    @Operation(summary = "get all products or products by catalogId")
    public List<ProductDTO> getProducts(@RequestParam(name="catalogId", required = false) Long catalogId) {
        List<Product> products;

        if(catalogId != null) {
            products = productService.getProducts(catalogId);
        } else {
            products = productService.getAll();
        }

        return products.stream()
                .map(ProductMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Создание нового товара.
     * Перед созданием выполняется валидация входных данных при помощи {@link ProductValidator}
     * В случае ошибок возвращается строка с перечнем ошибок.
     * Если товар создан успешно - возвращает {@code "CREATED"}
     * Иначе - {@code "FAILED}
     * @param productDTO - DTO товара, который необходимо создать
     * @return строковый статус выполнения операции
     */
    @Auditable(action = "create new product")
    @PostMapping
    @Operation(summary = "create new product")
    public ResponseEntity createProduct(@RequestBody ProductDTO productDTO,
                                        HttpServletRequest request) throws AccessDeniedException {
        AccessUtil.checkPermission(request, Permission.CREATE_PRODUCT);
        List<String> errors = ProductValidator.validate(productDTO);
        if (!errors.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(errors);
        }

        Product entity = ProductMapper.INSTANCE.toEntity(productDTO);
        Product saved = productService.createProduct(entity);

        if (saved == null){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating product");
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ProductMapper.INSTANCE.toDTO(saved));
    }

    /**
     * Удаление товара по его id.
     * Если товар удален успешно - возвращает {@code "DELETED"}.
     * Иначе - возвращает {@code "FAILED"}.
     * @param id - id товара, который необходимо удалить.
     * @return строковый статус выполнения операции.
     */
    @Auditable(action = "delete product")
    @DeleteMapping
    @Operation(summary = "delete product by id")
    public ResponseEntity<Void> deleteProduct(@RequestParam(name = "id", required = true) Long id,
                                              HttpServletRequest request) throws AccessDeniedException {
        AccessUtil.checkPermission(request, Permission.DELETE_PRODUCT);
        boolean deleted = productService.deleteProduct(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Обновление товара по его id
     * Перед обновлением выполняется валидация входных данных с помощью {@link ProductValidator}
     * Если обновление прошло успешно - возвращает {@code "UPDATED"}
     * Иначе - возвращает {@code "FAILED}
     * @param productDTO DTO товара, который необходимо обновить
     * @return строковый статус выполнения операции
     */
    @Auditable(action = "edit product")
    @PutMapping
    @Operation(summary = "update product")
    public ResponseEntity<?> updateProduct(@RequestBody ProductDTO productDTO, HttpServletRequest request) throws AccessDeniedException {
        AccessUtil.checkPermission(request, Permission.EDIT_PRODUCT);
        List<String> errors = ProductValidator.validate(productDTO);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Product entity = ProductMapper.INSTANCE.toEntity(productDTO);
        boolean updated = productService.updateProduct(entity);

        if (!updated) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ProductMapper.INSTANCE.toDTO(entity));
    }
}
