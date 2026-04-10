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

    private AuthService authService;
    private TenantDAO tenantDAO;
    private StaffDAO staffDAO;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("LoginController doGet failed: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            if (authService == null) {
                authService = new AuthService();
            }

            String mode = request.getParameter("mode");
            String email = request.getParameter("email");
            String remember = request.getParameter("remember");

            AuthResult authResult = null;

            if ("OTP".equalsIgnoreCase(mode)) {
                String otp = request.getParameter("otp");

                if (email == null || otp == null || email.isBlank() || otp.isBlank()) {
                    request.setAttribute("error", "Vui lòng nhập email và OTP.");
                    request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
                    return;
                }

                HttpSession existingSession = request.getSession(false);
                boolean isForgotFlow = existingSession != null
                        && email.trim().equalsIgnoreCase((String) existingSession.getAttribute("fp_email"));

                if (isForgotFlow) {
                    authResult = authService.loginByResetOtp(email, otp);
                } else {
                    authResult = authService.loginByOtp(email, otp);
                }

                if (authResult == null) {
                    request.setAttribute("error", "OTP không đúng hoặc đã hết hạn.");
                    request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
                    return;
                }

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
                    request.setAttribute("error",
                            "Sai thông tin đăng nhập hoặc tài khoản chưa set mật khẩu (hãy dùng OTP lần đầu).");
                    request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
                    return;
                }
            }

            HttpSession session = request.getSession(true);
            session.setAttribute("auth", authResult);

            if ("on".equals(remember)) {
                if (authResult.getTenant() != null) {
                    if (!"ACTIVE".equalsIgnoreCase(authResult.getTenant().getAccountStatus())) {
                        response.sendRedirect(request.getContextPath() + resolveRedirect(authResult));
                        return;
                    }
                }

                String token = TokenUtil.generateToken();

                if (tenantDAO == null) {
                    tenantDAO = new TenantDAO();
                }
                if (staffDAO == null) {
                    staffDAO = new StaffDAO();
                }

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

            response.sendRedirect(request.getContextPath() + resolveRedirect(authResult));

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("LoginController doPost failed: " + e.getMessage(), e);
        }
    }

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
}