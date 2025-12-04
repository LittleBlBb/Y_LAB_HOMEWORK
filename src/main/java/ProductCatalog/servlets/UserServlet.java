package ProductCatalog.servlets;

import ProductCatalog.annotations.Auditable;
import ProductCatalog.dto.UserDTO;

import ProductCatalog.mappers.UserMapper;
import ProductCatalog.models.User;
import ProductCatalog.services.implemetations.UserService;
import ProductCatalog.validators.UserValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    private UserService userService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        this.userService = (UserService) getServletContext().getAttribute("userService");
    }

    @Auditable(action = "get all users")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<UserDTO> dtoList = userService.getAll().stream()
                .map(UserMapper.INSTANCE::toDTO)
                .toList();
        resp.setContentType("application/json");
        mapper.writeValue(resp.getWriter(), dtoList);
    }

    @Auditable(action = "registration")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDTO dto = mapper.readValue(req.getInputStream(), UserDTO.class);

        resp.setContentType("application/json");

        List<String> errors = UserValidator.validate(dto);
        if (!errors.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            mapper.writeValue(resp.getWriter(),
                    Map.of("validation_errors", errors)
            );
            return;
        }
        User entity = UserMapper.INSTANCE.toEntity(dto);
        boolean created = userService.register(entity);

        if (created){
            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(resp.getWriter(), UserMapper.INSTANCE.toDTO(entity));
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), "User not created");
        }
    }
}