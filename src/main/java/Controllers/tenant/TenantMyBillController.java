/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.tenant;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import DALs.Bill.BillDAO;
import DALs.Bill.PaymentConfirmBillDAO;
import Models.authentication.AuthResult;
import Models.dto.ManagerBillRowDTO;
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
        PaymentConfirmBillDAO pd = new PaymentConfirmBillDAO();
        Bill b = bd.getCurrentBillForTenant(tenant_id);

        Payment pending = null;
        List<BillDetail> listBillDetail = null;
        String payment_qr = null;
        BigDecimal totalAmount = BigDecimal.ZERO;
        boolean allowPayment = false;
        if (b != null) {
            pending = pd.getPendingPaymentByBillId(b.getBillId());
            listBillDetail = bd.getListBillDetailByBillId(b.getBillId());
            payment_qr = bd.getQRFromContractByBillId(b.getBillId());
            totalAmount = bd.totalAmount(b.getBillId());
             //lay ngay tháng hien tai
             //
            LocalDate today = LocalDate.now();
            // lay tháng bill
            LocalDate billMonth = b.getBillMonth().toLocalDate();
            
            // tháng bill + 1 tháng
            LocalDate nextMonth = billMonth.plusMonths(1);
            
            //month hien tai = month ke tiep moi cho chuyn
            allowPayment = today.getMonthValue() == nextMonth.getMonthValue()
                    && today.getYear() == nextMonth.getYear();
        }

        if (payment_qr == null) {
            payment_qr = "/assets/images/qr/myqr.png";
        }
        String RoomNumber = bd.getRoomNumberByTenantId(tenant_id);
        BigDecimal totalTenantUnpaid = bd.getTotalTenantUnpaid(tenant_id);
        Payment lastPayment = bd.getLastPaidAmountByTenant(tenant_id);
        
        List<ManagerBillRowDTO> listTenantBills = bd.listBillForTenant(tenant_id);

        request.setAttribute("allowPayment", allowPayment);
        request.setAttribute("totalAmount", totalAmount);
        request.setAttribute("RoomNumber", RoomNumber);
        request.setAttribute("Bill", b);
        request.setAttribute("totalTenantUnpaid", totalTenantUnpaid);
        request.setAttribute("lastPayment", lastPayment);
        request.setAttribute("ListBillDetail", listBillDetail);
        request.setAttribute("billTenant", listTenantBills);
        request.setAttribute("totalBills", listTenantBills.size());
        request.setAttribute("pendingPayment", pending);
        request.setAttribute("qr", payment_qr);
        request.getRequestDispatcher("/views/tenant/myBill.jsp").forward(request, response);
    }

}
