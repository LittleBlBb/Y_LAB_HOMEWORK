package ProductCatalog.controllers;

import ProductCatalog.constants.Role;
import ProductCatalog.constants.SessionAttributes;
import ProductCatalog.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.mock.web.MockHttpServletRequest;

public abstract class BaseControllerTest {

    protected MockHttpServletRequest request;
    protected User admin;

    @BeforeEach
    void setUpBase() {
        request = new MockHttpServletRequest();
        admin = new User(1L, "admin", "pass", Role.ADMIN);
        request.getSession().setAttribute(SessionAttributes.USER, admin);
    }

    protected MockHttpServletRequest createNotLoggedInRequest() {
        return new MockHttpServletRequest();
    }

    protected MockHttpServletRequest createUserRequest(User user) {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.getSession().setAttribute(SessionAttributes.USER, user);
        return req;
    }
}
