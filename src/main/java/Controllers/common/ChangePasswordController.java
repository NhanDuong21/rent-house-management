package Controllers.common;

import java.io.IOException;

import Models.authentication.AuthResult;
import Models.common.ServiceResult;
import Services.auth.PasswordService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Duong Thien Nhan - CE190741
 */
public class ChangePasswordController extends HttpServlet {

    private final PasswordService passwordService = new PasswordService();

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false);
        AuthResult auth = (session == null) ? null : (AuthResult) session.getAttribute("auth");

        if (auth == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String oldPassword = request.getParameter("old_password");
        String newPassword = request.getParameter("new_password");
        String confirmPassword = request.getParameter("confirm_password");

        try {
            ServiceResult result = passwordService.changePassword(auth, oldPassword, newPassword, confirmPassword);

            if (result.isOk()) {
                session.setAttribute("auth", auth);
                response.sendRedirect(request.getContextPath() + "/profile?pwd=1");
            } else {
                response.sendRedirect(request.getContextPath()
                        + "/profile?err=1&code=" + result.getMessage());
            }

        } catch (IOException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/profile?err=1&code=EXCEPTION");
        }
    }
}
