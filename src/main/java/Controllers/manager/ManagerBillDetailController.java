/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.manager;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import DALs.Bill.BillDAO;
import Models.entity.Bill;
import Models.entity.BillDetail;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author To Thi Thao Trang - CE191027
 */
@WebServlet(name = "ManagerBillDetailController",urlPatterns = {"/manager/bills/detail"})
public class ManagerBillDetailController extends HttpServlet {
 
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BillDAO bd = new BillDAO();
        int billId = Integer.parseInt(request.getParameter("billId"));
        Bill bill = bd.findBillDetailByIdForManager(billId);
        List<BillDetail> listBillDetail = bd.getListBillDetailByBillId(billId);
        BigDecimal totalAmount = bd.totalAmount(billId);
        String payment_qr =bd.getQRFromContractByBillId(billId);
        String roomNumber = bd.getStringRoomnumber(billId);
        if (payment_qr == null) {
            payment_qr = "/assets/images/qr/myqr.png";
        }
        request.setAttribute("totalAmount", totalAmount);
        request.setAttribute("roomNumber", roomNumber);
        request.setAttribute("bill", bill);
        request.setAttribute("ListBillDetail", listBillDetail);
        request.setAttribute("qr", payment_qr);
       request.getRequestDispatcher("/views/manager/billDetail.jsp").forward(request, response);
    }

    

    
}
