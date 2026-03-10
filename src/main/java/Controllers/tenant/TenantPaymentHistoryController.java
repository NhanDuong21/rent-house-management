package Controllers.tenant;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

import java.io.IOException;
import java.util.List;

import DALs.Bill.PaymentConfirmBillDAO;
import Models.authentication.AuthResult;
import Models.dto.PaymentHistoryRowDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author To Thi Thao Trang - CE191027
 */
@WebServlet(name = "TenantPaymentHistoryController", urlPatterns = {"/tenant/paymentHistory"})
public class TenantPaymentHistoryController extends HttpServlet {
 
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
         HttpSession session = request.getSession();
        AuthResult auth = (AuthResult) session.getAttribute("auth");

        if (auth == null || auth.getTenant() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        int tenant_id = auth.getTenant().getTenantId();
        PaymentConfirmBillDAO pd = new PaymentConfirmBillDAO();
        List<PaymentHistoryRowDTO> list = pd.getAllPaymentHistoryByTenantId(tenant_id);
        request.setAttribute("paymentHistory", list);
        request.getRequestDispatcher("/views/tenant/paymentHistory.jsp") .forward(request, response);
    }

     
}
