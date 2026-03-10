/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.manager;

import Models.entity.Tenant;
import Services.tenant.TenantService;
import java.io.IOException;
import java.io.PrintWriter;
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
            /* TODO output your page here. You may use following sample code. */
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

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        TenantService service = new TenantService();
        List<Tenant> list;

        // Xử lý toggle status
        String action = request.getParameter("action");
        if ("toggleStatus".equals(action)) {
            String idStr = request.getParameter("id");
            String keyword = request.getParameter("keyword");
            String pageStr = request.getParameter("page");

            try {
                int id = Integer.parseInt(idStr);
                Tenant tenant = service.findById(id);

                // Nếu đang ACTIVE → muốn chuyển sang LOCKED → kiểm tra contract
                if (tenant != null && "ACTIVE".equals(tenant.getAccountStatus())) {
                    if (service.hasActiveContract(id)) {
                        // Tenant còn hợp đồng chưa kết thúc → không cho phép LOCK
                        // Forward lại trang với thông báo lỗi
                        int currentPageErr = 1;
                        if (pageStr != null) {
                            try { currentPageErr = Math.max(1, Integer.parseInt(pageStr)); }
                            catch (NumberFormatException ignored) {}
                        }

                        int totalRecords;
                        List<Tenant> listErr;
                        final int PAGE_SIZE = 10;
                        if (keyword == null || keyword.trim().isEmpty()) {
                            keyword = null;
                            listErr = service.getTenantsPaged(currentPageErr, PAGE_SIZE);
                            totalRecords = service.countAllTenants();
                        } else {
                            listErr = service.searchTenantPaged(keyword, currentPageErr, PAGE_SIZE);
                            totalRecords = service.countSearchTenant(keyword);
                        }
                        int totalPagesErr = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
                        if (totalPagesErr < 1) totalPagesErr = 1;

                        request.setAttribute("tenants", listErr);
                        request.setAttribute("keyword", keyword);
                        request.setAttribute("currentPage", currentPageErr);
                        request.setAttribute("totalPages", totalPagesErr);
                        request.setAttribute("totalRecords", totalRecords);
                        request.setAttribute("lockError",
                                "Cannot lock tenant \"" + tenant.getFullName()
                                + "\" because they still have an active contract. "
                                + "Please ensure all contracts are ENDED or CANCELLED first.");

                        request.getRequestDispatcher("/views/manager/managerTenant.jsp")
                                .forward(request, response);
                        return;
                    }
                }

                service.toggleStatus(id);
            } catch (NumberFormatException ignored) {
            }

            String redirect = request.getContextPath() + "/manager/tenants";
            boolean hasQuery = false;
            if (keyword != null && !keyword.isBlank()) {
                redirect += "?keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8");
                hasQuery = true;
            }
            if (pageStr != null && !pageStr.isBlank()) {
                redirect += (hasQuery ? "&" : "?") + "page=" + pageStr;
            }
            response.sendRedirect(redirect);
            return;
        }

        String keyword = request.getParameter("keyword");
        final int PAGE_SIZE = 10;

        // Lấy số trang hiện tại
        int currentPage = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try { currentPage = Math.max(1, Integer.parseInt(pageParam)); }
            catch (NumberFormatException ignored) {}
        }

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

        request.setAttribute("tenants", list);
        request.setAttribute("keyword", keyword);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);

        request.getRequestDispatcher("/views/manager/managerTenant.jsp")
                .forward(request, response);
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
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}