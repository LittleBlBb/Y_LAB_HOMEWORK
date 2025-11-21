package ProductCatalog.Servlets;

import ProductCatalog.DTO.UserDTO;

import ProductCatalog.Mappers.UserMapper;
import ProductCatalog.Services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    private UserService userService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        this.userService = (UserService) getServletContext().getAttribute("userService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<UserDTO> dtoList = userService.getAll().stream()
                .map(UserMapper.INSTANCE::toDTO)
                .toList();
        resp.setContentType("application/json");
        mapper.writeValue(resp.getWriter(), dtoList);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDTO dto = mapper.readValue(req.getInputStream(), UserDTO.class);

        boolean created = userService.register(UserMapper.INSTANCE.toEntity(dto));

        resp.setStatus(created ? HttpServletResponse.SC_CREATED : HttpServletResponse.SC_BAD_REQUEST);
    }
}
