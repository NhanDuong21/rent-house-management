package Controllers.admin;

import java.io.IOException;

import Services.admin.AccountService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminCreateAccountController extends HttpServlet {

    private final AccountService service = new AccountService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/views/admin/create-account.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String role = request.getParameter("role");
        String fullName = request.getParameter("fullName");
        String identityCode = request.getParameter("identityCode");
        String phoneNumber = request.getParameter("phoneNumber");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String dob = request.getParameter("dob");
        String genderParam = request.getParameter("gender");
        String password = request.getParameter("password");

        try {
            int gender = (genderParam != null && !genderParam.isEmpty())
                    ? Integer.parseInt(genderParam)
                    : 0;

            service.createAccount(
                    role,
                    fullName,
                    identityCode,
                    phoneNumber,
                    email,
                    address,
                    dob,
                    gender,
                    password
            );

            response.sendRedirect(request.getContextPath()
                    + "/admin/accounts?success=Account created successfully.");

        } catch (RuntimeException e) {
            request.setAttribute("error", e.getMessage());

            request.setAttribute("role", role);
            request.setAttribute("fullName", fullName);
            request.setAttribute("identityCode", identityCode);
            request.setAttribute("phoneNumber", phoneNumber);
            request.setAttribute("email", email);
            request.setAttribute("address", address);
            request.setAttribute("dob", dob);
            request.setAttribute("gender", genderParam);

            request.setAttribute("focusPassword", true);

            request.getRequestDispatcher("/views/admin/create-account.jsp")
                    .forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Admin Create Account Controller";
    }
}
