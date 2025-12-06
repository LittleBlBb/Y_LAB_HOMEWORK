package ProductCatalog.controllers;

import ProductCatalog.annotations.Auditable;
import ProductCatalog.validators.ProductValidator;
import ProductCatalog.dto.ProductDTO;
import ProductCatalog.mappers.ProductMapper;
import ProductCatalog.models.Product;
import ProductCatalog.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@Tag(name = "products", description = "operations with products")
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
    @Operation(summary = "getting all products or products by catalogId")
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
    @Auditable
    @PostMapping
    @Operation(summary = "create new product")
    public String createProduct(@RequestBody ProductDTO productDTO) {
        List<String> errors = ProductValidator.validate(productDTO);
        if (!errors.isEmpty()) {
            return errors.stream().collect(Collectors.joining("\n"));
        }

        boolean created = productService.createProduct(ProductMapper.INSTANCE.toEntity(productDTO));

        if (created) {
            return "CREATED";
        } else {
            return "FAILED";
        }
    }

    /**
     * Удаление товара по его id.
     * Если товар удален успешно - возвращает {@code "DELETED"}.
     * Иначе - возвращает {@code "FAILED"}.
     * @param id - id товара, который необходимо удалить.
     * @return строковый статус выполнения операции.
     */
    @Auditable
    @DeleteMapping
    @Operation(summary = "delete product by id")
    public String deleteProduct(@RequestParam(name = "id", required = true) Long id) {
        boolean deleted = productService.deleteProduct(id);

        if (deleted) {
            return "DELETED";
        } else {
            return "FAILED";
        }
    }

    /**
     * Обновление товара по его id
     * Перед обновлением выполняется валидация входных данных с помощью {@link ProductValidator}
     * Если обновление прошло успешно - возвращает {@code "UPDATED"}
     * Иначе - возвращает {@code "FAILED}
     * @param productDTO DTO товара, который необходимо обновить
     * @return строковый статус выполнения операции
     */
    @Auditable
    @PutMapping
    @Operation(summary = "update product")
    public String updateProduct(@RequestBody ProductDTO productDTO) {
        List<String> errors = ProductValidator.validate(productDTO);

        if (!errors.isEmpty()) {
            return errors.stream().collect(Collectors.joining("\n"));
        }

        boolean updated = productService.updateProduct(ProductMapper.INSTANCE.toEntity(productDTO));

        if (updated) {
            return "UPDATED";
        } else {
            return "FAILED";
        }
    }
}
