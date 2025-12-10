package ProductCatalog.controllers;

import ProductCatalog.audit.annotations.Auditable;
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

@RestController
@RequestMapping("/api/login")
@Api(tags = "login")
public class LoginController {

    private final UserService userService;
    private final ObjectMapper mapper = new ObjectMapper();

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Auditable(action = "login")
    @PostMapping
    @ApiOperation("login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.badRequest().body("User with username " + username + " not found");
        }
        if (!password.equals(user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
        session.setAttribute(SessionAttributes.USER, user);
        return ResponseEntity.ok("Login successful");
    }
}
