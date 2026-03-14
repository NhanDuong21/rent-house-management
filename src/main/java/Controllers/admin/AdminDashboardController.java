/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.admin;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import Services.admin.DashboardService;

/**
 *
 * @author LapNH
 */
public class AdminDashboardController extends HttpServlet {
// Service dùng để lấy dữ liệu thống kê cho dashboard

    private final DashboardService service = new DashboardService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy dữ liệu từ Service
        int totalTenants = service.getTotalTenants();
        int availableRooms = service.getAvailableRooms();
        int maintenanceRequests = service.getMaintenanceRequests();
        int occupiedRooms = service.getOccupiedRooms();
        int activeContracts = service.getActiveContracts();
        double monthlyRevenue = service.getMonthlyRevenue();
        double totalRevenue = service.getTotalRevenue();

        // Gửi dữ liệu sang JSP
        request.setAttribute("totalTenants", totalTenants);
        request.setAttribute("availableRooms", availableRooms);
        request.setAttribute("maintenanceRequests", maintenanceRequests);
        request.setAttribute("occupiedRooms", occupiedRooms);
        request.setAttribute("activeContracts", activeContracts);
        request.setAttribute("monthlyRevenue", monthlyRevenue);
        request.setAttribute("totalRevenue", totalRevenue);

        // Forward tới trang dashboard
        request.getRequestDispatcher("/views/admin/dashboard.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
