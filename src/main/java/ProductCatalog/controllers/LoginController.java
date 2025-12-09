package ProductCatalog.controllers;

import ProductCatalog.constants.SessionAttributes;
import ProductCatalog.dto.LoginRequest;
import ProductCatalog.models.User;
import ProductCatalog.services.implementations.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api/login")
@Api(tags = "login")
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ApiOperation("login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest,
                                   HttpSession session) {
        String username = loginRequest.getUsername();

        User user = userService.findByUsername(loginRequest.getUsername());
        if (user == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "User not found"));
        }

        boolean valid = userService.checkPassword(user, loginRequest.getPassword());
        if (!valid) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid username or password"));
        }
        session.setAttribute(SessionAttributes.USER, user);
        return ResponseEntity.ok(Map.of("message", "Login successful"));
    }
}
