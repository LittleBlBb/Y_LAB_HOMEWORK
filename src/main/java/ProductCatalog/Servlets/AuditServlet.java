package ProductCatalog.Servlets;

import ProductCatalog.Models.AuditEntry;
import ProductCatalog.Services.AuditService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/audit")
public class AuditServlet extends HttpServlet {
    private AuditService auditService;

    @Override
    public void init() throws ServletException {
        auditService = (AuditService) getServletContext().getAttribute("auditService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<AuditEntry> logs = auditService.getAll();
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        out.println("[");
        for (int i = 0; i < logs.size(); i++) {
            AuditEntry auditEntry = logs.get(i);
            out.printf("{\"id\":%d,\"username\":\"%s\",\"action\":\"%s\",\"details\":\"%s\",\"timestamp\":\"%s\"}%s%n",
                    auditEntry.getId(), auditEntry.getUsername(), auditEntry.getAction(), auditEntry.getDetails(), auditEntry.getTimestamp(),
                    i < logs.size() - 1 ? "," : "");
        }
        out.println("]");
    }
}
