package ProductCatalog.controllers;

import ProductCatalog.models.User;
import ProductCatalog.services.UserService;
import ProductCatalog.validators.UserValidator;
import ProductCatalog.dto.UserDTO;
import ProductCatalog.mappers.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Tag(name = "users", description = "operations with users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "get all users")
    public List<UserDTO> getAllUsers() {
        return userService.getAll().stream()
                .map(UserMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    @Operation(summary = "register new user")
    public String registerUser(@RequestBody UserDTO userDTO) {
        List<String> errors = UserValidator.validate(userDTO);
        if (!errors.isEmpty()) {
            return errors.stream().collect(Collectors.joining("\n"));
        }
        User entity = UserMapper.INSTANCE.toEntity(userDTO);
        boolean created = userService.register(entity);

        if (created){
            return "CREATED";
        } else {
           return "FAILED";
        }
    }
}
