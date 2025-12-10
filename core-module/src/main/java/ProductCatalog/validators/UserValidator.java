package ProductCatalog.validators;

import ProductCatalog.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

public class UserValidator {

    public static List<String> validate(UserDTO dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getUsername() == null || dto.getUsername().isBlank())
            errors.add("username is required.");

        if (dto.getPassword() == null || dto.getPassword().isBlank())
            errors.add("password is required.");

        return errors;
    }
}