/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.tenant;

import DALs.utilities.Utilities_UsageDAO;
import DALs.utilities.utilitiesDAO;
import Models.authentication.AuthResult;
import Models.entity.Utility;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bui Nhu Y
 */
@WebServlet(name = "TenantUtilityController", urlPatterns = {"/tenant/utility"})
public class TenantUtilityController extends HttpServlet {

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
            out.println("<title>Servlet TenantUtilitiesController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TenantUtilitiesController at " + request.getContextPath() + "</h1>");
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
        HttpSession session = request.getSession(false);
        AuthResult auth = (AuthResult) session.getAttribute("auth");
        int tenantId = auth.getTenant().getTenantId();

        // Lấy danh sách extra utility để hiển thị checkbox
        utilitiesDAO dao = new utilitiesDAO();
        List<Utility> utilities = dao.getExtraUtility();

        // Lấy contractId → lấy list đã đăng ký để pre-check
        Utilities_UsageDAO usageDAO = new Utilities_UsageDAO();
        int contractId = usageDAO.getActiveContractIdByTenantId(tenantId);
        List<Integer> subscribedIds = usageDAO.getSubscribedUtilityIds(contractId);

        boolean isBillPaid = usageDAO.isBillLocked(tenantId);

        request.setAttribute("utility", utilities);
        request.setAttribute("subscribedIds", subscribedIds);
        request.setAttribute("isBillPaid", isBillPaid);
        request.getRequestDispatcher("/views/tenant/tenantUtility.jsp").forward(request, response);

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

        HttpSession session = request.getSession(false);
        AuthResult auth = (AuthResult) session.getAttribute("auth");
        int tenantId = auth.getTenant().getTenantId();

        Utilities_UsageDAO usageDAO = new Utilities_UsageDAO();

        // Chặn nếu bill đã PAID
        if (usageDAO.isBillLocked(tenantId)) {
            response.sendRedirect(request.getContextPath() + "/tenant/utility");
            return;
        }

        int contractId = usageDAO.getActiveContractIdByTenantId(tenantId);
        if (contractId == -1) {
            response.sendRedirect(request.getContextPath() + "/tenant/utility");
            return;
        }

        // Danh sách đang subscribed trong DB
        List<Integer> currentIds = usageDAO.getSubscribedUtilityIds(contractId);

        // Danh sách user vừa tick trên form
        String[] selectedArr = request.getParameterValues("utilityIds");
        List<Integer> newIds = new ArrayList<>();
        if (selectedArr != null) {
            for (String id : selectedArr) {
                newIds.add(Integer.parseInt(id));
            }
        }

        // Tính toán diff
        List<Integer> toRemove = new ArrayList<>(currentIds);
        toRemove.removeAll(newIds); // có trong DB nhưng không còn tick → xóa

        List<Integer> toAdd = new ArrayList<>(newIds);
        toAdd.removeAll(currentIds); // tick mới, chưa có trong DB → thêm

        usageDAO.removeUtilityUsages(contractId, toRemove);
        usageDAO.addMultipleUtilityUsage(contractId, toAdd);

        // Sync sang BILL_DETAIL
        int billId = usageDAO.getUnpaidBillIdByTenantId(tenantId);
        if (billId != -1) {
            usageDAO.syncToBillDetail(billId, contractId);
        }

        response.sendRedirect(request.getContextPath() + "/tenant/utility");
    }

    /**
     * Returns a short description of the servlet.
     *
     * @retur a String containing servlet description
     */
    @Override

    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
