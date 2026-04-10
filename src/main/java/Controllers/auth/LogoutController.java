package Controllers.auth;

import java.io.IOException;

import DALs.auth.StaffDAO;
import DALs.auth.TenantDAO;
import Models.authentication.AuthResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LogoutController extends HttpServlet {

    private TenantDAO tenantDAO;
    private StaffDAO staffDAO;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            HttpSession session = request.getSession(false);

            if (session != null) {
                AuthResult auth = (AuthResult) session.getAttribute("auth");

                if (auth != null) {
                    if (tenantDAO == null)
                        tenantDAO = new TenantDAO();
                    if (staffDAO == null)
                        staffDAO = new StaffDAO();

                    if (auth.getTenant() != null) {
                        tenantDAO.clearTokenForTenant(auth.getTenant().getTenantId());
                    }
                    if (auth.getStaff() != null) {
                        staffDAO.clearTokenForStaff(auth.getStaff().getStaffId());
                    }
                }

                session.invalidate();
            }

            Cookie c = new Cookie("REMEMBER_TOKEN", "");
            c.setMaxAge(0);
            c.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
            response.addCookie(c);

            response.sendRedirect(request.getContextPath() + "/home");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}