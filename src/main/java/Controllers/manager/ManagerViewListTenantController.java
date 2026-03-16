/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.manager;

import Models.entity.Tenant;
import Services.tenant.TenantService;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author ADMIN
 */
@WebServlet(name = "ManagerViewListTenant", urlPatterns = {"/manager/tenants"})
public class ManagerViewListTenantController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ManagerViewListTenant</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ManagerViewListTenant at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * Tự động đồng bộ status tenant dựa trên hợp đồng trước khi hiển thị danh sách.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        TenantService service = new TenantService();

        // Tự động cập nhật status toàn bộ tenant dựa trên điều kiện hợp đồng
        // - Có contract active (không phải ENDED/CANCELLED) → ACTIVE
        // - Không có contract active → LOCKED
        // - PENDING giữ nguyên
        service.syncAllTenantStatuses();

        String keyword = request.getParameter("keyword");
        final int PAGE_SIZE = 10;

        // Lấy số trang hiện tại
        int currentPage = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try { currentPage = Math.max(1, Integer.parseInt(pageParam)); }
            catch (NumberFormatException ignored) {}
        }

        List<Tenant> list;
        int totalRecords;

        if (keyword == null || keyword.trim().isEmpty()) {
            keyword = null;
            list = service.getTenantsPaged(currentPage, PAGE_SIZE);
            totalRecords = service.countAllTenants();
        } else {
            list = service.searchTenantPaged(keyword, currentPage, PAGE_SIZE);
            totalRecords = service.countSearchTenant(keyword);
        }

        int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
        if (totalPages < 1) totalPages = 1;

        // Map tenantId -> roomNumber cho contract đang active
        Map<Integer, String> activeRoomMap = service.getActiveRoomMap();

        request.setAttribute("tenants", list);
        request.setAttribute("activeRoomMap", activeRoomMap);
        request.setAttribute("keyword", keyword);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);

        request.getRequestDispatcher("/views/manager/managerTenant.jsp")
                .forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }

}