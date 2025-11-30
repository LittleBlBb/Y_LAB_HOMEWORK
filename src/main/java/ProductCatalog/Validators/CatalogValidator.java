package ProductCatalog.Validators;

import ProductCatalog.dto.CatalogDTO;

import java.util.ArrayList;
import java.util.List;

public class CatalogValidator {

    public static List<String> validate(CatalogDTO dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getName() == null || dto.getName().isBlank())
            errors.add("name is required.");

        return errors;
    }
}
