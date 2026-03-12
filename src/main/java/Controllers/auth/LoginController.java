package Controllers.auth;

import java.io.IOException;

import DALs.auth.StaffDAO;
import DALs.auth.TenantDAO;
import Models.authentication.AuthResult;
import Services.auth.AuthService;
import Utils.security.TokenUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * @author Duong Thien Nhan - CE190741
 */
public class LoginController extends HttpServlet {

    private final AuthService authService = new AuthService();
    private final TenantDAO tenantDAO = new TenantDAO();
    private final StaffDAO staffDAO = new StaffDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String mode    = request.getParameter("mode");
        String email   = request.getParameter("email");
        String remember = request.getParameter("remember");

        AuthResult authResult = null;

        if ("OTP".equalsIgnoreCase(mode)) {
            String otp = request.getParameter("otp");

            if (email == null || otp == null || email.isBlank() || otp.isBlank()) {
                request.setAttribute("error", "Vui lòng nhập email và OTP.");
                request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
                return;
            }

            // Kiểm tra session xem có phải đang dùng forgot-password flow không
            HttpSession existingSession = request.getSession(false);
            boolean isForgotFlow = existingSession != null
                    && email.trim().equalsIgnoreCase((String) existingSession.getAttribute("fp_email"));

            if (isForgotFlow) {
                // Dùng hàm mới — hỗ trợ cả Tenant lẫn Staff reset password
                authResult = authService.loginByResetOtp(email, otp);
            } else {
                // Giữ nguyên hàm cũ — FIRST_LOGIN cho Tenant
                authResult = authService.loginByOtp(email, otp);
            }

            if (authResult == null) {
                request.setAttribute("error", "OTP không đúng hoặc đã hết hạn.");
                request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
                return; // ← fix bug trang đen
            }

            // Xóa session forgot-password sau khi login thành công
            if (isForgotFlow && existingSession != null) {
                existingSession.removeAttribute("fp_email");
                existingSession.removeAttribute("fp_user_type");
            }

        } else {
            String password = request.getParameter("password");

            if (email == null || password == null || email.isBlank() || password.isBlank()) {
                request.setAttribute("error", "Vui lòng nhập email và mật khẩu.");
                request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
                return;
            }

            authResult = authService.login(email, password);
            if (authResult == null) {
                request.setAttribute("error", "Sai thông tin đăng nhập hoặc tài khoản chưa set mật khẩu (hãy dùng OTP lần đầu).");
                request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
                return;
            }
        }

        // Lưu session
        HttpSession session = request.getSession(true);
        session.setAttribute("auth", authResult);

        // Remember me — giữ nguyên logic cũ
        if ("on".equals(remember)) {
            if (authResult.getTenant() != null) {
                if (!"ACTIVE".equalsIgnoreCase(authResult.getTenant().getAccountStatus())) {
                    response.sendRedirect(request.getContextPath() + resolveRedirect(authResult));
                    return;
                }
            }
            String token = TokenUtil.generateToken();
            if (authResult.getTenant() != null) {
                tenantDAO.updateTokenForTenant(authResult.getTenant().getTenantId(), token);
            } else if (authResult.getStaff() != null) {
                staffDAO.updateTokenForStaff(authResult.getStaff().getStaffId(), token);
            }
            Cookie cookie = new Cookie("REMEMBER_TOKEN", token);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(45 * 24 * 60 * 60);
            cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
            response.addCookie(cookie);
        }

        // Redirect đúng trang theo role
        response.sendRedirect(request.getContextPath() + resolveRedirect(authResult));
    }

    /**
     * Thêm mới — redirect theo role.
     * Chỉnh path cho đúng URL mapping thực tế trong project.
     */
    private String resolveRedirect(AuthResult auth) {
        String role = auth.getRole();
        if (role == null) return "/home";
        return switch (role.toUpperCase()) {
            case "ADMIN"   -> "/home";
            case "MANAGER" -> "/home";
            default        -> "/home"; // TENANT + fallback
        };
    }
}