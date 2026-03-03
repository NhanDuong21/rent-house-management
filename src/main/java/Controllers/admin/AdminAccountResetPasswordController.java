package Controllers.admin;

import java.io.IOException;

import Models.authentication.AuthResult;
import Models.common.ServiceResult;
import Services.staff.StaffService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Duong Thien Nhan - CE190741
 */
public class AdminAccountResetPasswordController extends HttpServlet {

    private final StaffService staffService = new StaffService();

    private AuthResult getAuth(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session == null) ? null : (AuthResult) session.getAttribute("auth");
    }

    private boolean isAdmin(AuthResult auth) {
        return auth != null && auth.getStaff() != null && "ADMIN".equalsIgnoreCase(auth.getRole());
    }

    private boolean isAjax(HttpServletRequest req) {
        String xrw = req.getHeader("X-Requested-With");
        String accept = req.getHeader("Accept");
        return "XMLHttpRequest".equalsIgnoreCase(xrw) || (accept != null && accept.contains("application/json"));
    }

    private void writeJson(HttpServletResponse resp, int status, boolean ok, String message)
            throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json; charset=UTF-8");

        String safe = (message == null) ? "" : message.replace("\\", "\\\\").replace("\"", "\\\"");
        resp.getWriter().write("{\"ok\":" + ok + ",\"message\":\"" + safe + "\"}");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        AuthResult auth = getAuth(request);
        if (!isAdmin(auth)) {
            if (isAjax(request)) {
                writeJson(response, 401, false, "NO_PERMISSION");
            } else {
                response.sendRedirect(request.getContextPath() + "/login");
            }
            return;
        }

        String accountType = request.getParameter("accountType");
        String accountIdRaw = request.getParameter("accountId");
        String newPassword = request.getParameter("newPassword");
        String confirm = request.getParameter("confirmPassword");

        if (accountIdRaw == null || accountIdRaw.isBlank()) {
            if (isAjax(request)) {
                writeJson(response, 400, false, "ID không hợp lệ");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/accounts?error=ID%20kh%C3%B4ng%20h%E1%BB%A3p%20l%E1%BB%87");
            }
            return;
        }

        int accountId;
        try {
            accountId = Integer.parseInt(accountIdRaw.trim());
        } catch (NumberFormatException e) {
            if (isAjax(request)) {
                writeJson(response, 400, false, "ID không hợp lệ");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/accounts?error=ID%20kh%C3%B4ng%20h%E1%BB%A3p%20l%E1%BB%87");
            }
            return;
        }

        ServiceResult result = staffService.adminResetPassword(auth, accountType, accountId, newPassword, confirm);

        if (isAjax(request)) {
            if (result.isOk()) {
                writeJson(response, 200, true, "Reset password thành công");
            } else {
                writeJson(response, 400, false, result.getMessage());
            }
            return;
        }

        // Fallback redirect (non-AJAX)
        if (result.isOk()) {
            response.sendRedirect(request.getContextPath()
                    + "/admin/accounts?success=Reset%20password%20th%C3%A0nh%20c%C3%B4ng");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/accounts?error=" + java.net.URLEncoder.encode(result.getMessage(), "UTF-8"));
        }
    }
}
