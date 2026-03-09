/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.manager;

import java.io.IOException;
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
@WebServlet(name = "ManagerInputBillController", urlPatterns = {"/manager/billing/input"})
public class ManagerInputBillController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int billId = Integer.parseInt(request.getParameter("billId"));
        BillDAO b = new BillDAO();
        Bill bill = b.findBillDetailByIdForManager(billId);
        List<BillDetail> listDetail = b.getListBillDetailByBillId(billId);
        String roomNumber = b.getStringRoomnumber(billId);
        request.setAttribute("roomNumber", roomNumber);
        request.setAttribute("bill", bill);
        request.setAttribute("listDetail", listDetail);
        request.getRequestDispatcher("/views/manager/inputMeter.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int billId = Integer.parseInt(request.getParameter("billId"));
        int oldElectric = Integer.parseInt(request.getParameter("oldElectric"));
        int newElectric = Integer.parseInt(request.getParameter("newElectric"));
        int oldWater = Integer.parseInt(request.getParameter("oldWater"));
        int newWater = Integer.parseInt(request.getParameter("newWater"));

        // validate
        if (newElectric <= oldElectric) {
            request.setAttribute("error", "New electric meter must be greater than old meter.");
            request.getRequestDispatcher("/views/manager/inputMeter.jsp").forward(request, response);
            return;
        }

        if (newWater <= oldWater) {
            request.setAttribute("error", "New water meter must be greater than old meter.");
            request.getRequestDispatcher("/views/manager/inputMeter.jsp").forward(request, response);
            return;
        }

        BillDAO b = new BillDAO();
        try {
            b.finalizeBill(billId, oldElectric, newElectric, oldWater, newWater);
            response.sendRedirect(request.getContextPath() + "/manager/billing");
        } catch (Exception ex) {
            request.setAttribute("error", "Error!");
            request.getRequestDispatcher("/views/manager/inputMeter.jsp").forward(request, response);
        }

    }

}
