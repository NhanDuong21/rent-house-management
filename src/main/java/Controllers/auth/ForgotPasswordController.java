package Controllers.auth;

import java.io.IOException;

import DALs.auth.StaffDAO;
import DALs.auth.TenantDAO;
import Models.authentication.AuthResult;
import Models.entity.Staff;
import Models.entity.Tenant;
import Services.auth.ForgotPasswordService;
import Utils.security.HashUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * POST /forgot-password
 * step=email → gửi OTP, lưu fp_email vào session
 * step=verifyOtp → kiểm tra OTP, nếu đúng lưu fp_verified=true vào session
 * step=resetPassword → đổi password, tự động đăng nhập, trả về redirect URL
 *
 * @author Dang Huu Thanh - CE191422
 */
@WebServlet(name = "ForgotPasswordController", urlPatterns = { "/forgot-password" })
public class ForgotPasswordController extends HttpServlet {

    private final ForgotPasswordService service = new ForgotPasswordService();
    private final TenantDAO tenantDAO = new TenantDAO();
    private final StaffDAO staffDAO = new StaffDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String step = request.getParameter("step");
        if (step == null)
            step = "";

        switch (step.toLowerCase()) {
            case "email" -> handleEmailStep(request, response);
            case "verifyotp" -> handleVerifyOtp(request, response);
            case "resetpassword" -> handleResetPassword(request, response);
            default -> sendJson(response, false, "Yêu cầu không hợp lệ.");
        }
    }

    // ─── Bước 1: nhập email → gửi OTP ───────────────────────────────────────────

    private void handleEmailStep(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String email = request.getParameter("email");

        if (email == null || email.isBlank()) {
            sendJson(response, false, "Vui lòng nhập địa chỉ email.");
            return;
        }

        email = email.trim().toLowerCase();
        String result = service.sendForgotPasswordOtp(email);

        switch (result) {
            case "SENT" -> {
                HttpSession session = request.getSession(true);
                session.setAttribute("fp_email", email);
                session.removeAttribute("fp_verified"); // reset nếu có từ lần trước
                sendJson(response, true, "OTP đã được gửi.");
            }
            case "EMAIL_NOT_FOUND" ->
                sendJson(response, false, "Email không tồn tại trong hệ thống.");
            case "MAIL_FAILED" ->
                sendJson(response, false, "Không thể gửi email. Vui lòng kiểm tra lại địa chỉ hoặc thử lại sau.");
            default ->
                sendJson(response, false, "Có lỗi xảy ra, vui lòng thử lại.");
        }
    }

    // ─── Bước 2: nhập OTP → verify ───────────────────────────────────────────────

    private void handleVerifyOtp(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        String email = (session == null) ? null : (String) session.getAttribute("fp_email");

        if (email == null) {
            sendJson(response, false, "Phiên làm việc đã hết hạn. Vui lòng thử lại từ đầu.");
            return;
        }

        String otp = request.getParameter("otp");
        if (otp == null || otp.isBlank()) {
            sendJson(response, false, "Vui lòng nhập mã OTP.");
            return;
        }

        boolean valid = service.verifyForgotPasswordOtp(email, otp.trim());
        if (!valid) {
            sendJson(response, false, "OTP không đúng hoặc đã hết hạn.");
            return;
        }

        // Đánh dấu đã xác thực OTP thành công
        session.setAttribute("fp_verified", true);
        sendJson(response, true, "OTP hợp lệ.");
    }

    // ─── Bước 3: đặt mật khẩu mới → tự động đăng nhập ───────────────────────────

    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        String email = (session == null) ? null : (String) session.getAttribute("fp_email");
        Boolean verified = (session == null) ? null : (Boolean) session.getAttribute("fp_verified");

        // Bảo vệ: phải qua bước verifyOtp trước
        if (email == null || !Boolean.TRUE.equals(verified)) {
            sendJson(response, false, "Phiên làm việc không hợp lệ. Vui lòng thực hiện lại từ đầu.");
            return;
        }

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (newPassword == null || newPassword.isBlank()) {
            sendJson(response, false, "Vui lòng nhập mật khẩu mới.");
            return;
        }
        if (newPassword.length() < 6 || newPassword.length() > 64) {
            sendJson(response, false, "Mật khẩu phải từ 6 đến 64 ký tự.");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            sendJson(response, false, "Xác nhận mật khẩu không khớp.");
            return;
        }

        String newHash = HashUtil.md5(newPassword);
        boolean updated = false;
        AuthResult authResult = null;

        // Thử cập nhật Tenant
        Integer tenantId = tenantDAO.findTenantIdByEmail(email);
        if (tenantId != null) {
            updated = tenantDAO.updatePasswordForTenant(tenantId, newHash);
            if (updated) {
                Tenant t = tenantDAO.findById(tenantId);
                if (t != null) {
                    authResult = new AuthResult("TENANT", t, null);
                }
            }
        }

        // Thử cập nhật Staff
        if (!updated) {
            Integer staffId = staffDAO.findStaffIdByEmail(email);
            if (staffId != null) {
                updated = staffDAO.updatePasswordForStaff(staffId, newHash);
                if (updated) {
                    Staff s = staffDAO.findById(staffId);
                    if (s != null) {
                        authResult = new AuthResult(s.getStaffRole(), null, s);
                    }
                }
            }
        }

        if (!updated || authResult == null) {
            sendJson(response, false, "Cập nhật mật khẩu thất bại. Vui lòng thử lại.");
            return;
        }

        // Xóa dữ liệu forgot-password khỏi session
        session.removeAttribute("fp_email");
        session.removeAttribute("fp_verified");

        // Tự động đăng nhập — lưu auth vào session
        session.setAttribute("auth", authResult);

        // Trả về redirect URL theo role
        String redirectUrl = request.getContextPath() + resolveRedirect(authResult);
        sendJsonWithRedirect(response, true, "Đổi mật khẩu thành công.", redirectUrl);
    }

    // ─── helpers ─────────────────────────────────────────────────────────────────

    private String resolveRedirect(AuthResult auth) {
        String role = auth.getRole();
        if (role == null)
            return "/home";
        return switch (role.toUpperCase()) {
            case "ADMIN" -> "/home";
            case "MANAGER" -> "/home";
            default -> "/home";
        };
    }

    private void sendJson(HttpServletResponse response, boolean success, String message)
            throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String safeMsg = message.replace("\\", "\\\\").replace("\"", "\\\"");
        response.getWriter().write(
                "{\"success\":" + success + ",\"message\":\"" + safeMsg + "\"}");
    }

    private void sendJsonWithRedirect(HttpServletResponse response, boolean success, String message, String redirectUrl)
            throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String safeMsg = message.replace("\\", "\\\\").replace("\"", "\\\"");
        String safeUrl = redirectUrl.replace("\\", "\\\\").replace("\"", "\\\"");
        response.getWriter().write(
                "{\"success\":" + success + ",\"message\":\"" + safeMsg + "\",\"redirect\":\"" + safeUrl + "\"}");
    }
}