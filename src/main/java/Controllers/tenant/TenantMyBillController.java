/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.tenant;

import java.io.IOException;
import java.math.BigDecimal;

import DALs.Bill.BillDAO;
import Models.authentication.AuthResult;
import Models.entity.Bill;
import Models.entity.Payment;
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
@WebServlet(name = "TenantMyBillController", urlPatterns = {"/tenant/bill"})
public class TenantMyBillController extends HttpServlet {

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
        BillDAO bd = new BillDAO();
        Bill b = bd.getCurrentBillForTenant(tenant_id);
        BigDecimal totalTenantUnpaid = bd.getTotalTenantUnpaid(tenant_id);
        Payment lastPayment = bd.getLastPaidAmountByTenant(tenant_id);
        String RoomNumber = bd.getRoomNumberByTenantId(tenant_id);
        BigDecimal totalAmount = BigDecimal.ZERO;
        if(b != null) {
            totalAmount = bd.totalAmount(b.getBillId());
        }
        request.setAttribute("totalAmount", totalAmount);
        request.setAttribute("RoomNumber", RoomNumber);
        request.setAttribute("Bill", b);
        request.setAttribute("totalTenantUnpaid", totalTenantUnpaid);
        request.setAttribute("lastPayment", lastPayment);
        request.getRequestDispatcher("/views/tenant/myBill.jsp").forward(request, response);
    }

}
