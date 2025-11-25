package ProductCatalog.Servlets;

import ProductCatalog.DTO.UserDTO;
import ProductCatalog.Mappers.UserMapper;
import ProductCatalog.Models.User;
import ProductCatalog.Services.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServletTest {

    private UserServlet servlet;
    private UserService userService;
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() throws Exception {
        userService = mock(UserService.class);
        mapper = new ObjectMapper();

        ServletContext context = mock(ServletContext.class);
        when(context.getAttribute("userService")).thenReturn(userService);

        servlet = new UserServlet() {
            @Override
            public ServletContext getServletContext() {
                return context;
            }
        };

        servlet.init();
    }

    private jakarta.servlet.ServletInputStream createInputStream(String json) {
        return new jakarta.servlet.ServletInputStream() {
            private int pos = 0;
            private final byte[] data = json.getBytes();

            @Override
            public int read() {
                return pos < data.length ? data[pos++] : -1;
            }

            @Override public boolean isFinished() { return pos >= data.length; }
            @Override public boolean isReady() { return true; }
            @Override public void setReadListener(jakarta.servlet.ReadListener readListener) {}
        };
    }

    @Test
    public void testDoGet_returnsUserList_asDTO() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getServletContext()).thenReturn(servlet.getServletContext());

        StringWriter writer = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(writer));

        List<User> users = List.of(
                new User(1L, "Alice", "pass1", "USER"),
                new User(2L, "Bob", "pass2", "ADMIN")
        );

        when(userService.getAll()).thenReturn(users);

        servlet.doGet(req, resp);

        String resultJson = writer.toString().trim();

        List<UserDTO> actualList = mapper.readValue(resultJson, new TypeReference<>() {});
        List<UserDTO> expectedList = List.of(
                new UserDTO(1L, "Alice", "pass1", "USER"),
                new UserDTO(2L, "Bob", "pass2", "ADMIN")
        );

        assertEquals(expectedList, actualList);
        verify(resp).setContentType("application/json");
    }

    @Test
    public void testDoPost_createsUser_success() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(req.getServletContext()).thenReturn(servlet.getServletContext());


        UserDTO dto = new UserDTO(
                0, "Charlie", "pass3", "USER"
        );

        String json = mapper.writeValueAsString(dto);
        when(req.getInputStream()).thenReturn(createInputStream(json));

        StringWriter writer = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(writer));

        when(userService.register(any())).thenAnswer(invocation -> {
            User u =  invocation.getArgument(0);
            u.setId(1L);
            return true;
        });

        servlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_CREATED);

        UserDTO actual = mapper.readValue(writer.toString(), UserDTO.class);

        UserDTO expected = UserMapper.INSTANCE.toDTO(new User(
                1L, "Charlie", "pass3", "USER"
        ));

        assertEquals(expected, actual);
        verify(resp).setContentType("application/json");
    }

    @Test
    public void testDoPost_validationError() throws Exception {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(req.getServletContext()).thenReturn(servlet.getServletContext());

        String json = """
                {"username": "Charlie", "password": "pass1", "role":""}
                """;

        when(req.getInputStream()).thenReturn(createInputStream(json));

        StringWriter writer = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(writer));

        servlet.doPost(req, resp);

        verify(resp).setStatus(HttpServletResponse.SC_BAD_REQUEST);

        Map<String, Object> body =
                mapper.readValue(writer.toString(), Map.class);

        assertEquals(true, body.containsKey("validation_errors"));
    }
}