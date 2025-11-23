package ProductCatalog.Validators;

import ProductCatalog.DTO.ProductDTO;

import java.util.ArrayList;
import java.util.List;

public class ProductValidator {

    public static List<String> validate(ProductDTO dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getCatalogId() == null)
            errors.add("catalog_id is required.");
        else if (dto.getCatalogId() <= 0)
            errors.add("catalog_id must be greater than 0.");

        if (dto.getName() == null || dto.getName().isBlank())
            errors.add("name is required.");

        if (dto.getPrice() <= 0)
            errors.add("price must be positive.");

        if (dto.getBrand() == null || dto.getBrand().isBlank())
            errors.add("brand is required.");

        if (dto.getCategory() == null || dto.getCategory().isBlank())
            errors.add("category is required.");

        return errors;
    }
}
