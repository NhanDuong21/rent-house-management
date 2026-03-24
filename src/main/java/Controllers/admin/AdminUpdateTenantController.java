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
import Models.entity.Tenant;
import java.sql.Date;

public class AdminUpdateTenantController extends HttpServlet {

    ManageAccountDAO dao = new ManageAccountDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));

        Tenant tenant = dao.getTenantById(id);

        request.setAttribute("tenant", tenant);

        request.getRequestDispatcher("/views/admin/updateTenant.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Tenant t = new Tenant();

        t.setTenantId(Integer.parseInt(request.getParameter("id")));
        t.setFullName(request.getParameter("fullName"));
        t.setPhoneNumber(request.getParameter("phone"));
        t.setEmail(request.getParameter("email"));
        t.setAddress(request.getParameter("address"));
        t.setIdentityCode(request.getParameter("identityCode"));

        t.setGender(Integer.valueOf(request.getParameter("gender")));

        t.setDateOfBirth(Date.valueOf(request.getParameter("dateOfBirth")));

        t.setAccountStatus(request.getParameter("status"));

        dao.updateTenant(t);

        response.sendRedirect(
                request.getContextPath() + "/admin/accounts?success=Tenant updated successfully"
        );
    }

}
