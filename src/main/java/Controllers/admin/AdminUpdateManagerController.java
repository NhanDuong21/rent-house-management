/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.admin;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import DALs.admin.ManageAccountDAO;
import Models.dto.AdminAccountRowDTO;

/**
 *
 * @author LapNH
 */
public class AdminUpdateManagerController extends HttpServlet {

    ManageAccountDAO dao = new ManageAccountDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));

        AdminAccountRowDTO manager = dao.getManagerById(id);

        request.setAttribute("manager", manager);

        request.getRequestDispatcher("/views/admin/updateManager.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        int gender = Integer.parseInt(request.getParameter("gender"));
        String dob = request.getParameter("dateOfBirth");
        String identity = request.getParameter("identityCode");
        String status = request.getParameter("status");

        boolean updated = dao.updateManager(id, name, email, phone, gender, dob, identity, status);

        if (updated) {
            response.sendRedirect(
                    request.getContextPath() + "/admin/accounts?success=Manager updated successfully"
            );
        } else {
            response.sendRedirect(
                    request.getContextPath() + "/admin/accounts?error=Update manager failed"
            );
        }
    }
}
