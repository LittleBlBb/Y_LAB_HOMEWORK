package ProductCatalog.utils;

import ProductCatalog.constants.Permission;
import ProductCatalog.models.User;
import ProductCatalog.servlets.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.file.AccessDeniedException;

public class AccessUtil {

    public static User getUser(HttpServletRequest req) throws AccessDeniedException {
        HttpSession session = req.getSession(false);

        if (session == null)
            throw new AccessDeniedException("You are not logged in");

        Object u = session.getAttribute(SessionAttributes.USER);
        if (!(u instanceof User user))
            throw new AccessDeniedException("You are not logged in");

        return user;
    }

    public static void checkPermission(HttpServletRequest req, Permission perm)
            throws AccessDeniedException {

        User user = getUser(req);

        if (!user.getRole().hasPermission(perm))
            throw new AccessDeniedException("You do not have permission: " + perm);
    }
}