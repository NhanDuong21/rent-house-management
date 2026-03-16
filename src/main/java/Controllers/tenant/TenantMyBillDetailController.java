package Controllers.tenant;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import DALs.Bill.BillDAO;
import DALs.Bill.PaymentConfirmBillDAO;
import Models.authentication.AuthResult;
import Models.entity.Bill;
import Models.entity.BillDetail;
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
@WebServlet(urlPatterns = {"/tenant/billdetail"})
public class TenantMyBillDetailController extends HttpServlet {

   
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
        
        int billId = Integer.parseInt(request.getParameter("billId"));
        BillDAO bd = new BillDAO();
        PaymentConfirmBillDAO pd = new PaymentConfirmBillDAO();
        Bill billDetail = bd.findBillDetailByIdForTenant(billId, tenant_id);
        if (billDetail == null) {
            response.sendRedirect(request.getContextPath() + "/tenant/bill");
            return;
        }
        List<BillDetail> listBillDetail = bd.getListBillDetailByBillId(billId);
        Payment pendingPayment = pd.getPendingPaymentByBillId(billDetail.getBillId());
        BigDecimal totalAmount = bd.totalAmount(billId);
        String RoomNumber = bd.getRoomNumberByTenantId(tenant_id);
        String payment_qr = bd.getQRFromContractByBillId(billDetail.getBillId());
        if (payment_qr == null) {
            payment_qr = "/assets/images/qr/myqr.png";
        }
        request.setAttribute("billDetail", billDetail);
        request.setAttribute("ListBillDetail", listBillDetail);
        request.setAttribute("totalAmount", totalAmount);
        request.setAttribute("RoomNumber", RoomNumber);
        request.setAttribute("pendingPayment", pendingPayment);
        request.setAttribute("qr", payment_qr);

        request.getRequestDispatcher("/views/tenant/myBillDetail.jsp").forward(request, response);
    }

}
