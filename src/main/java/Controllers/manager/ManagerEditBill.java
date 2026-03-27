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

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
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

            BillDAO dao = new BillDAO();
            Bill bill = dao.findBillDetailByIdForManager(billId);

            if (bill == null) {
                response.sendRedirect(request.getContextPath() + "/manager/billing");
                return;
            }

            // chỉ cho edit khi UNPAID hoặc PENDING
            if (!bill.getStatus().equals("UNPAID") || "PENDING".equals(paymentStatus)) {

                request.setAttribute("error", "Only UNPAID or PENDING bill can be edited.");
                request.setAttribute("bill", bill);

                request.getRequestDispatcher("/views/manager/editBill.jsp")
                        .forward(request, response);
                return;
            }

            // VALIDATE ELECTRIC
            if (newElectric < oldElectric) {
                request.setAttribute("error", "New electric meter must be greater than old meter.");
                request.setAttribute("bill", bill);
                request.getRequestDispatcher("/views/manager/editBill.jsp")
                        .forward(request, response);
                return;
            }
            // VALIDATE WATER
            if (newWater < oldWater) {
                request.setAttribute("error", "New water meter must be greater than old meter.");
                request.setAttribute("bill", bill);
                request.getRequestDispatcher("/views/manager/editBill.jsp")
                        .forward(request, response);
                return;
            }
            boolean result = dao.updateBillMeter(billId, billMonth, dueDate, oldElectric, newElectric, oldWater, newWater);

            if (!result) {
                request.setAttribute("error", "Update bill failed");
                request.setAttribute("bill", bill);
                request.getRequestDispatcher("/views/manager/editBill.jsp").forward(request, response);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/manager/billing");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/manager/billing");
        }
    }
}
    
    


