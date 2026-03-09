/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.tenant;

import java.io.IOException;

import Models.authentication.AuthResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import DALs.Bill.PaymentConfirmBillDAO;
import java.math.BigDecimal;

/**
 *
 * @author To Thi Thao Trang - CE191027
 */
@WebServlet(name = "TenantRequestPaymentController", urlPatterns = {"/tenant/payment"})
public class TenantRequestPaymentController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        AuthResult auth = (AuthResult) session.getAttribute("auth");
        PaymentConfirmBillDAO pm = new PaymentConfirmBillDAO();
        if (auth == null || auth.getTenant() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        int tenant_id = auth.getTenant().getTenantId();
        int billId = Integer.parseInt(request.getParameter("billId"));
        BigDecimal amount = new BigDecimal(request.getParameter("amount"));
        String method = request.getParameter("method");
        try {
            pm.createPaymentForTenant(billId, method, amount);
            response.sendRedirect(request.getContextPath() +"/tenant/bill");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
