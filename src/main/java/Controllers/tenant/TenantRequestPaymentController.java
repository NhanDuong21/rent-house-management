/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.tenant;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

import DALs.Bill.PaymentConfirmBillDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author To Thi Thao Trang - CE191027
 */
@WebServlet(name = "TenantRequestPaymentController", urlPatterns = { "/tenant/payment" })
public class TenantRequestPaymentController extends HttpServlet {

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PaymentConfirmBillDAO pm = new PaymentConfirmBillDAO();

        int billId = Integer.parseInt(request.getParameter("billId"));
        BigDecimal amount = new BigDecimal(request.getParameter("amount"));
        String method = request.getParameter("method");
        try {
            pm.createPaymentForTenant(billId, method, amount);
            response.sendRedirect(request.getContextPath() + "/tenant/bill");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

    }

}
