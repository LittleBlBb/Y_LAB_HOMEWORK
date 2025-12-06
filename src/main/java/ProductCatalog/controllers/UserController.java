package ProductCatalog.controllers;

import ProductCatalog.annotations.Auditable;
import ProductCatalog.constants.Permission;
import ProductCatalog.models.User;
import ProductCatalog.services.implementations.UserService;
import ProductCatalog.utils.AccessUtil;
import ProductCatalog.validators.UserValidator;
import ProductCatalog.dto.UserDTO;
import ProductCatalog.mappers.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Контроллер для управления пользователями.
 * Предоставляет REST-эндпоинты для получения и создания пользователей.
 * Использует {@link ProductCatalog.services.implementations.UserService} для выполнения бизнес-логики и {@link ProductCatalog.mappers.UserMapper}
 * для преобразования сущностей в DTO и обратно.
 *
 * Аннотация {@link Auditable} применяется для аудита действий.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "users", description = "operations with users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Получение списка пользователей.
     * @return список пользователей в виде {@link UserDTO}
     */
    @Auditable(action = "get all users")
    @GetMapping
    @Operation(summary = "get all users")
    public List<UserDTO> getAllUsers(HttpServletRequest request) throws AccessDeniedException {
        AccessUtil.checkPermission(request, Permission.MANAGE_USERS);
        return userService.getAll().stream()
                .map(UserMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Регистрация нового пользователя
     * Перед созданием происходит валидация входных данных при помощи {@link UserValidator}
     * В случае ошибок возвращается строка с перечнем ошибок.
     * Если регистрация успешна - возвращает {@code "REGISTERED"}
     * Иначе - возвращает {@code "FAILED}
     * @param userDTO - DTO пользователя, которого необходимо создать
     * @return строковое представление статуса выполнения операции
     */
    @Auditable(action = "registration")
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
            return "REGISTERED";
        } else {
           return "FAILED";
        }
    }
}
