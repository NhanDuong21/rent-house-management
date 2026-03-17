package Filters;

import java.io.IOException;

import DALs.auth.StaffDAO;
import DALs.auth.TenantDAO;
import Models.authentication.AuthResult;
import Models.entity.Staff;
import Models.entity.Tenant;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Duong Thien Nhan - CE190741
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    private final TenantDAO tenantDAO = new TenantDAO();
    private final StaffDAO staffDAO = new StaffDAO();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String ctx = request.getContextPath();
        String uri = request.getRequestURI();

        // ===== PUBLIC =====
        boolean isStatic = uri.startsWith(ctx + "/assets/");
        boolean isPublic = uri.equals(ctx + "/home")
                || uri.equals(ctx + "/login")
                || uri.equals(ctx + "/logout")
                || uri.equals(ctx + "/contact");

        if (isStatic || isPublic) {
            chain.doFilter(req, res);
            return;
        }

        // ===== SESSION =====
        HttpSession session = request.getSession(false);
        AuthResult auth = (session == null) ? null : (AuthResult) session.getAttribute("auth");

        // ===== REMEMBER ME =====
        if (auth == null) {
            String token = getCookieValue(request, "REMEMBER_TOKEN");
            if (token != null && !token.isBlank()) {

                Tenant tenant = tenantDAO.findByTokenForTenant(token);
                if (tenant != null) {
                    AuthResult ar = new AuthResult("TENANT", tenant, null);
                    request.getSession(true).setAttribute("auth", ar);
                    auth = ar;
                } else {
                    Staff staff = staffDAO.findByTokenForStaff(token);
                    if (staff != null) {
                        AuthResult ar = new AuthResult(staff.getStaffRole(), null, staff);
                        request.getSession(true).setAttribute("auth", ar);
                        auth = ar;
                    }
                }
            }
        }

        String role = (auth == null || auth.getRole() == null) ? "GUEST" : auth.getRole();

        // ===== TENANT ROUTE CHECK =====
        if (uri.startsWith(ctx + "/tenant/")) {

            if (!("TENANT".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role))) {
                response.sendRedirect(ctx + "/home");
                return;
            }

            // ===== FORCE SET PASSWORD (FIRST LOGIN) =====
            Tenant tenant = auth.getTenant();
            if (tenant != null) {

                // must_set_password trong DB là BIT, entity bạn chắc có getter boolean hoặc int
                boolean mustSet = tenant.isMustSetPassword(); // nếu lỗi thì xem note bên dưới

                boolean isSetPasswordPage = uri.equals(ctx + "/tenant/set-password");
                boolean allowSetPass = isSetPasswordPage || uri.equals(ctx + "/logout") || uri.startsWith(ctx + "/assets/");

                if (mustSet && !allowSetPass) {
                    response.sendRedirect(ctx + "/tenant/set-password");
                    return;
                }
            }

            // ===== CHECK PENDING TENANT =====
            if (tenant != null && "PENDING".equalsIgnoreCase(tenant.getAccountStatus())) {

                boolean allowContract
                        = uri.equals(ctx + "/tenant/contract")
                        || uri.startsWith(ctx + "/tenant/contract/")
                        || uri.equals(ctx + "/tenant/add-occupant");

                boolean allowSetPassword = uri.equals(ctx + "/tenant/set-password");

                if (!allowContract && !allowSetPassword) {
                    response.sendRedirect(ctx + "/tenant/contract");
                    return;
                }
            }
        }

        // ===== MANAGER =====
        if (uri.startsWith(ctx + "/manager/")
                && !("MANAGER".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role))) {
            response.sendRedirect(ctx + "/home");
            return;
        }
        // ===== ADMIN =====
        if (uri.startsWith(ctx + "/admin/") && !"ADMIN".equalsIgnoreCase(role)) {
            response.sendRedirect(ctx + "/home");
            return;
        }

        // ===== DISABLE BACK CACHE =====
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        chain.doFilter(req, res);
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie c : cookies) {
            if (name.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }
}
