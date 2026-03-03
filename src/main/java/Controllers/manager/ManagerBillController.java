/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.manager;

import java.io.IOException;
import java.util.List;

import DALs.Bill.BillDAO;
import Models.dto.ManagerBillRowDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author To Thi Thao Trang - CE191027
 */
@WebServlet(name = "ManagerBillController", urlPatterns = {"/manager/billing"})
public class ManagerBillController extends HttpServlet {

    private final BillDAO billDAO = new BillDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        int page = 1;
        int pageSize = 10;

        String pageParameter = request.getParameter("page");
        if (pageParameter != null) {
            page = Integer.parseInt(pageParameter);
        }
        int totalBills = billDAO.countSearchManagerBills(status, keyword);
        int totalPages = (int) Math.ceil((double) totalBills / pageSize);
        List<ManagerBillRowDTO> list = billDAO.getManagerBills(keyword, status, page, pageSize);
        request.setAttribute("totalBills", totalBills);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("bill", list);
        request.getRequestDispatcher("/views/manager/bills.jsp").forward(request, response);
    }

}
