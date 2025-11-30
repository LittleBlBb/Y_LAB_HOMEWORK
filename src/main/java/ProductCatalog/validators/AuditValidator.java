package ProductCatalog.validators;

import ProductCatalog.dto.AuditEntryDTO;

import java.util.ArrayList;
import java.util.List;

public class AuditValidator {

    public static List<String> validate(AuditEntryDTO dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getUsername() == null || dto.getUsername().isBlank())
            errors.add("username is required.");

        if (dto.getAction() == null || dto.getAction().isBlank())
            errors.add("action is required.");

        if (dto.getDetails() == null || dto.getDetails().isBlank())
            errors.add("details is required.");

        if (dto.getTimestamp() == null || dto.getTimestamp().isBlank())
            errors.add("timestamp is required.");

        return errors;
    }
}

