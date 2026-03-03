package Controllers.manager;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
import java.io.IOException;
import java.sql.SQLException;

import DALs.Bill.PaymentConfirmBillDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author ADMIN
 */
@WebServlet(name = "ManagerPaymentConfirmBillController", urlPatterns = {"/manager/bills/paymentConfirm"})
public class ManagerPaymentConfirmBillController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String billIdString = request.getParameter("billId");
        if (billIdString == null || billIdString.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/manager/billing");
            return;
        }
        int billId;
        try {
            billId = Integer.parseInt(billIdString);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/manager/billing");
            return;
        }
        PaymentConfirmBillDAO pcDAO = new PaymentConfirmBillDAO();
        try {
            pcDAO.confirmPaymentForManager(billId);
            response.sendRedirect(request.getContextPath() + "/manager/bills/detail?billId=" + billId);
        } catch (SQLException e) {
            request.setAttribute("errorMsg", e.getMessage());
            request.getRequestDispatcher("/manager/bills/detail").forward(request, response);;
        }
    }

}
