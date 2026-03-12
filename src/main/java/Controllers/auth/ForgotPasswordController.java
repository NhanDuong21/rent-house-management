package Controllers.auth;

import Services.auth.ForgotPasswordService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * POST /forgot-password  step=email  → JSON {success, message}
 *
 * @author Duong Thien Nhan - CE190741
 */
@WebServlet(name = "ForgotPasswordController", urlPatterns = {"/forgot-password"})
public class ForgotPasswordController extends HttpServlet {

    private final ForgotPasswordService service = new ForgotPasswordService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String step = request.getParameter("step");
        if ("email".equalsIgnoreCase(step)) {
            handleEmailStep(request, response);
        } else {
            sendJson(response, false, "Yêu cầu không hợp lệ.");
        }
    }

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
                sendJson(response, true, "OTP đã được gửi.");
            }
            case "EMAIL_NOT_FOUND" ->
                sendJson(response, false, "Email không tồn tại trong hệ thống.");
            case "MAIL_FAILED" ->
                // Email tìm thấy nhưng gửi mail thất bại — không tiết lộ email có tồn tại
                sendJson(response, false, "Không thể gửi email. Vui lòng kiểm tra lại địa chỉ hoặc thử lại sau.");
            default ->
                sendJson(response, false, "Có lỗi xảy ra, vui lòng thử lại.");
        }
    }

    private void sendJson(HttpServletResponse response, boolean success, String message)
            throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String safeMsg = message.replace("\\", "\\\\").replace("\"", "\\\"");
        response.getWriter().write(
                "{\"success\":" + success + ",\"message\":\"" + safeMsg + "\"}"
        );
    }
}