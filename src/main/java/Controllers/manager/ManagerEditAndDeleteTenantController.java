package Controllers.manager;

import Models.entity.Tenant;
import Models.common.ServiceResult;
import Services.tenant.TenantService;
import Utils.security.HashUtil;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Dang Huu Thanh - CE191422
 */
@WebServlet(urlPatterns = { "/manager/tenant/edit" })
public class ManagerEditAndDeleteTenantController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ManagerEditTenantController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ManagerEditTenantController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String page = request.getParameter("page");
        String keyword = request.getParameter("keyword");

        // ── Nhánh Reset Password ───────────────────────────────────────────────
        if ("resetPassword".equals(action)) {
            handleResetPassword(request, response, page, keyword);
            return;
        }

        // ── Nhánh Toggle Status ────────────────────────────────────────────────
        if ("toggleStatus".equals(action)) {
            handleToggleStatus(request, response, page, keyword);
            return;
        }

        // ── Nhánh Edit Tenant (mặc định) ──────────────────────────────────────
        int id = Integer.parseInt(request.getParameter("tenantId"));
        TenantService service = new TenantService();
        Tenant t = service.findById(id);

        if (t != null) {
            // Kiểm tra có active contract không — chỉ cho phép edit khi có
            if (!service.hasActiveContract(id)) {
                // Không có active contract → redirect về kèm lỗi
                StringBuilder redirectUrl = new StringBuilder(
                        request.getContextPath() + "/manager/tenants?error="
                                + java.net.URLEncoder.encode("Chỉ có thể chỉnh sửa tenant đang có hợp đồng active.",
                                        "UTF-8"));
                appendPageKeyword(redirectUrl, page, keyword);
                response.sendRedirect(redirectUrl.toString());
                return;
            }

            t.setFullName(request.getParameter("fullName"));
            t.setIdentityCode(request.getParameter("identityCode"));
            t.setPhoneNumber(request.getParameter("phoneNumber"));
            t.setEmail(request.getParameter("email"));
            t.setAddress(request.getParameter("address"));

            String dob = request.getParameter("dateOfBirth");
            if (dob != null && !dob.isEmpty()) {
                t.setDateOfBirth(java.sql.Date.valueOf(dob));
            }

            String gender = request.getParameter("gender");
            if (gender != null && !gender.isEmpty()) {
                t.setGender(Integer.parseInt(gender));
            }

            try {
                service.updateTenant(t);
            } catch (IllegalArgumentException ex) {
                StringBuilder redirectUrl = new StringBuilder(
                        request.getContextPath() + "/manager/tenants?error="
                                + java.net.URLEncoder.encode(ex.getMessage(), "UTF-8"));
                appendPageKeyword(redirectUrl, page, keyword);
                response.sendRedirect(redirectUrl.toString());
                return;
            }
        }

        // Redirect về đúng trang sau khi edit thành công
        StringBuilder redirectUrl = new StringBuilder(request.getContextPath() + "/manager/tenants?success=updated");
        appendPageKeyword(redirectUrl, page, keyword);
        response.sendRedirect(redirectUrl.toString());
    }

    /**
     * Xử lý toggle status cho tenant.
     * - LOCKED → ACTIVE: luôn cho phép, không cần điều kiện.
     * - ACTIVE → LOCKED: chỉ cho phép khi không có hợp đồng active.
     * - PENDING: không cho toggle.
     */
    private void handleToggleStatus(HttpServletRequest request, HttpServletResponse response,
            String page, String keyword) throws IOException {

        int tenantId = Integer.parseInt(request.getParameter("tenantId"));
        TenantService service = new TenantService();

        ServiceResult result = service.toggleStatus(tenantId);
        String resultMsg = result.getMessage(); // "ACTIVE" | "LOCKED" = success; error codes = fail

        String errorMsg = null;
        switch (resultMsg) {
            case "HAS_ACTIVE_CONTRACT":
                errorMsg = "Không thể khóa tenant đang có hợp đồng active.";
                break;
            case "CANNOT_TOGGLE_PENDING":
                errorMsg = "Không thể thay đổi trạng thái tenant PENDING.";
                break;
            case "NOT_FOUND":
                errorMsg = "Không tìm thấy tenant.";
                break;
            case "UPDATE_FAILED":
                errorMsg = "Thay đổi trạng thái thất bại. Vui lòng thử lại.";
                break;
            default:
                // "ACTIVE" hoặc "LOCKED" → thành công
                break;
        }

        if (errorMsg != null) {
            StringBuilder redirectUrl = new StringBuilder(
                    request.getContextPath() + "/manager/tenants?error="
                            + java.net.URLEncoder.encode(errorMsg, "UTF-8"));
            appendPageKeyword(redirectUrl, page, keyword);
            response.sendRedirect(redirectUrl.toString());
            return;
        }

        StringBuilder redirectUrl = new StringBuilder(
                request.getContextPath() + "/manager/tenants?success=status_changed");
        appendPageKeyword(redirectUrl, page, keyword);
        response.sendRedirect(redirectUrl.toString());
    }

    /**
     * Xử lý reset password cho tenant.
     * Chỉ cho phép khi tenant có active contract.
     */
    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response,
            String page, String keyword) throws IOException {

        int tenantId = Integer.parseInt(request.getParameter("tenantId"));
        TenantService service = new TenantService();

        // Kiểm tra active contract
        if (!service.hasActiveContract(tenantId)) {
            StringBuilder redirectUrl = new StringBuilder(
                    request.getContextPath() + "/manager/tenants?error="
                            + java.net.URLEncoder
                                    .encode("Chỉ có thể reset password cho tenant đang có hợp đồng active.", "UTF-8"));
            appendPageKeyword(redirectUrl, page, keyword);
            response.sendRedirect(redirectUrl.toString());
            return;
        }

        String newPassword = request.getParameter("newPassword");
        if (newPassword == null || newPassword.isBlank() || newPassword.length() < 6) {
            StringBuilder redirectUrl = new StringBuilder(
                    request.getContextPath() + "/manager/tenants?error="
                            + java.net.URLEncoder.encode("Mật khẩu không hợp lệ (tối thiểu 6 ký tự).", "UTF-8"));
            appendPageKeyword(redirectUrl, page, keyword);
            response.sendRedirect(redirectUrl.toString());
            return;
        }

        Tenant t = service.findById(tenantId);
        if (t == null) {
            StringBuilder redirectUrl = new StringBuilder(
                    request.getContextPath() + "/manager/tenants?error="
                            + java.net.URLEncoder.encode("Không tìm thấy tenant.", "UTF-8"));
            appendPageKeyword(redirectUrl, page, keyword);
            response.sendRedirect(redirectUrl.toString());
            return;
        }

        String newHash = HashUtil.md5(newPassword);
        // Cập nhật password qua DAO (dùng method sẵn có)
        DALs.auth.TenantDAO tenantDAO = new DALs.auth.TenantDAO();
        boolean ok = tenantDAO.adminResetPasswordForTenant(tenantId, newHash);

        if (!ok) {
            StringBuilder redirectUrl = new StringBuilder(
                    request.getContextPath() + "/manager/tenants?error="
                            + java.net.URLEncoder.encode("Reset password thất bại. Vui lòng thử lại.", "UTF-8"));
            appendPageKeyword(redirectUrl, page, keyword);
            response.sendRedirect(redirectUrl.toString());
            return;
        }

        // Thành công
        StringBuilder redirectUrl = new StringBuilder(
                request.getContextPath() + "/manager/tenants?success=password_reset");
        appendPageKeyword(redirectUrl, page, keyword);
        response.sendRedirect(redirectUrl.toString());
    }

    /**
     * Helper: append page và keyword vào redirectUrl nếu có.
     */
    private void appendPageKeyword(StringBuilder url, String page, String keyword)
            throws java.io.UnsupportedEncodingException {
        boolean hasQuery = url.indexOf("?") >= 0;
        if (keyword != null && !keyword.isBlank()) {
            url.append(hasQuery ? "&" : "?")
                    .append("keyword=")
                    .append(java.net.URLEncoder.encode(keyword, "UTF-8"));
            hasQuery = true;
        }
        if (page != null && !page.isBlank()) {
            url.append(hasQuery ? "&" : "?").append("page=").append(page);
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}