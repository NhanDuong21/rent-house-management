/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.manager;

import java.io.IOException;
import java.sql.Date;

import DALs.Bill.BillDAO;
import DALs.Bill.PaymentConfirmBillDAO;
import Models.entity.Bill;
import Services.bill.BillService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author To Thi Thao Trang - CE191027
 */
@WebServlet(name = "ManagerEditBill", urlPatterns = {"/manager/billing/editBill"})
public class ManagerEditBill extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int billId = Integer.parseInt(request.getParameter("billId"));
        BillDAO dao = new BillDAO();
        PaymentConfirmBillDAO p = new PaymentConfirmBillDAO();
        String paymentStatus = p.getLatestPaymentStatus(billId);
        Bill bill = dao.findBillDetailByIdForManager(billId);
        request.setAttribute("paymentStatus", paymentStatus);
        request.setAttribute("bill", bill);
        request.getRequestDispatcher("/views/manager/editBill.jsp").forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
         try {
            int billId = Integer.parseInt(request.getParameter("billId"));
            String paymentStatus = request.getParameter("paymentStatus");

            Date billMonth = Date.valueOf(request.getParameter("billMonth"));
            Date dueDate = Date.valueOf(request.getParameter("dueDate"));

            int oldElectric = Integer.parseInt(request.getParameter("oldElectric"));
            int newElectric = Integer.parseInt(request.getParameter("newElectric"));
            int oldWater = Integer.parseInt(request.getParameter("oldWater"));
            int newWater = Integer.parseInt(request.getParameter("newWater"));

            BillService service = new BillService();
            String error = service.updateBill(billId, paymentStatus, billMonth, dueDate, oldElectric, newElectric, oldWater, newWater);

            //validate
            if (error != null) {
                BillDAO dao = new BillDAO();
                PaymentConfirmBillDAO pmcDAO = new PaymentConfirmBillDAO();
                Bill bill = dao.findBillDetailByIdForManager(billId);
                String latestPaymentStatus = pmcDAO.getLatestPaymentStatus(billId);
                request.setAttribute("error", error);
                request.setAttribute("bill", bill);
                request.setAttribute("paymentStatus", latestPaymentStatus);
                request.getRequestDispatcher("/views/manager/editBill.jsp").forward(request, response);
            }

            response.sendRedirect(request.getContextPath() + "/manager/billing");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/manager/billing");
        }
    }
}
    
    


