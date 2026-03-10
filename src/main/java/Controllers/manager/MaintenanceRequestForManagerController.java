/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.manager;

import java.io.IOException;
import java.util.List;

import DALs.maintenanceRequest.MaintenanceRequestDAO;
import Models.dto.MaintenanceRequestDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author truon
 */
public class MaintenanceRequestForManagerController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        MaintenanceRequestDAO dao = new MaintenanceRequestDAO();
        String action = request.getParameter("action");
        if ("edit".equals(action)) {
            showMaintenanceDetail(request, response, dao);
            return;
        }
        int pageIndex = 1;
        int pageSize = 10;
        String page = request.getParameter("page");

        if (page != null) {
            try {
                pageIndex = Integer.parseInt(page);
            } catch (NumberFormatException e) {
                pageIndex = 1;
            }
        }
        String search = request.getParameter("search");
        if (search == null) {
            search = "";
        }
        int totalRequest = dao.countRequest(search);
        int totalPage = (int) Math.ceil((double) totalRequest / pageSize);

        if (totalPage == 0) {
            totalPage = 1;
        }

        if (pageIndex < 1) {
            pageIndex = 1;
        }

        if (pageIndex > totalPage) {
            pageIndex = totalPage;
        }
        List<MaintenanceRequestDTO> list = dao.getAllRequests(pageIndex, pageSize, search);
        request.setAttribute("requests", list);
        request.setAttribute("totalRequest", totalRequest);
        request.setAttribute("pageIndex", pageIndex);
        request.setAttribute("totalPage", totalPage);
        request.getRequestDispatcher("/views/manager/maintenanceListForManager.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        MaintenanceRequestDAO dao = new MaintenanceRequestDAO();

        try {
            int id = Integer.parseInt(request.getParameter("requestId"));
            String status = request.getParameter("status");
            MaintenanceRequestDTO maintenance = dao.getRequestById(id);
            dao.updateStatus(id, status);

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/manager/maintenance");
    }

    private void showMaintenanceDetail(HttpServletRequest request, HttpServletResponse response, MaintenanceRequestDAO dao)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        MaintenanceRequestDTO maintenance = dao.getRequestById(id);
        request.setAttribute("maintenance", maintenance);
        request.getRequestDispatcher("/views/manager/editMaintenance.jsp")
                .forward(request, response);
    }
}
