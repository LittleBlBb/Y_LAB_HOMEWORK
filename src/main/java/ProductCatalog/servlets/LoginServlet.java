package ProductCatalog.servlets;

import ProductCatalog.annotations.Auditable;
import ProductCatalog.dto.LoginRequest;
import ProductCatalog.models.User;
import ProductCatalog.services.implemetations.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserService userService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        this.userService = (UserService) getServletContext().getAttribute("userService");
    }

    @Auditable(action = "login")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LoginRequest login = mapper.readValue(req.getInputStream(), LoginRequest.class);
        String username = login.getUsername();
        String password = login.getPassword();

        User user = userService.findByUsername(username);
        if (user == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), "User with username " + username + " not found");
        } else {
            if (password.equals(user.getPassword())) {
                HttpSession session = req.getSession();
                session.setAttribute(SessionAttributes.USER, user);
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                mapper.writeValue(resp.getWriter(), "Invalid username or password");
            }
        }
    }
}