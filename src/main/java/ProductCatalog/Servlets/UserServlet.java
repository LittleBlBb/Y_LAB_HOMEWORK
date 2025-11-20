package ProductCatalog.Servlets;

import ProductCatalog.Models.User;

import ProductCatalog.Services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() throws ServletException {
        this.userService = (UserService) getServletContext().getAttribute("userService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<User> users = userService.getAllUsers();
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        out.println("[");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            out.printf("{\"id\":%d,\"username\":\"%s\",\"role\":\"%s\"}%s%n",
                    user.getId(), user.getUsername(), user.getRole(),
                    i > users.size() - 1 ? "," : "");
        }
        out.println("]");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        boolean created = userService.register(username, password);
        resp.setStatus(created ? HttpServletResponse.SC_CREATED : HttpServletResponse.SC_BAD_REQUEST);
    }
}
