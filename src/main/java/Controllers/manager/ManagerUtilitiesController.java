/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.manager;

import DALs.utilities.Utilities_UsageDAO;
import DALs.utilities.utilitiesDAO;
import Models.entity.Utility;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Bui Nhu Y
 */
@WebServlet(name = "ManagerUtilities", urlPatterns = {"/manager/utilities"})
public class ManagerUtilitiesController extends HttpServlet {

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
            out.println("<title>Servlet ManagerUtilities</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ManagerUtilities at " + request.getContextPath() + "</h1>");
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
        String action = request.getParameter("action");
        utilitiesDAO dao = new utilitiesDAO();
        Utilities_UsageDAO usdao = new Utilities_UsageDAO();
        if (action == null) {
            action = "all";
        }
        switch (action) {
            case "all":
                request.setAttribute("successMsg", request.getSession().getAttribute("successMsg"));
                request.setAttribute("errorMsg", request.getSession().getAttribute("errorMsg"));
                request.getSession().removeAttribute("successMsg");
                request.getSession().removeAttribute("errorMsg");

                List<Utility> listU = dao.getManagerUtilities();
                request.setAttribute("utilities", listU);
                request.getRequestDispatcher("/views/manager/utilities.jsp").forward(request, response);
                break;

            case "add":
                List<Utility> listAdd = dao.getManagerUtilities();
                request.setAttribute("utilities", listAdd);
                request.getRequestDispatcher("/views/manager/utilities.jsp").forward(request, response);
                break;

            case "delete":
                int idDelete = Integer.parseInt(request.getParameter("id"));
                if (dao.isUtilityUsedInBill(idDelete)) {
                    request.getSession().setAttribute("errorMsg", "Cannot be deleted! This utility is being used in the invoice.");
                } else {
                    boolean deleted = dao.deleteUtilities(idDelete);

                    if (deleted) {
                        request.getSession().setAttribute("successMsg", "Extension successfully removed!");
                    } else {
                        request.getSession().setAttribute("errorMsg", "Clear failure, please try again!");
                    }
                }
                response.sendRedirect(request.getContextPath() + "/manager/utilities");
                break;

            case "edit":
                int idEdit = Integer.parseInt(request.getParameter("id"));
                Utility uEdit = dao.getUtilityById(idEdit);
                List<Utility> listEdit = dao.getManagerUtilities();
                request.setAttribute("utilities", listEdit);
                request.setAttribute("editUtility", uEdit);
                request.getRequestDispatcher("/views/manager/utilities.jsp").forward(request, response);
                break;

            case "subscribers":
                int idSub = Integer.parseInt(request.getParameter("id"));
                String nameSub = request.getParameter("name");

                int page = 1;
                int pageSize = 10;

                String pageParam = request.getParameter("page");
                if (pageParam != null) {
                    try {
                        page = Integer.parseInt(pageParam);
                        if (page < 1) {
                            page = 1;
                        }
                    } catch (NumberFormatException e) {
                        page = 1;
                    }
                }

                int totalRecords = usdao.countSubscribersByUtilityId(idSub);
                int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

                if (page > totalPages && totalPages > 0) {
                    page = totalPages;
                }

                List<Utility> subscribers = usdao.getSubscribersByUtilityIdPaging(idSub, page, pageSize);

                request.setAttribute("subscribers", subscribers);
                request.setAttribute("utilityName", nameSub);
                request.setAttribute("utilityId", idSub);
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", totalPages);
                request.setAttribute("totalRecords", totalRecords);

                request.getRequestDispatcher("/views/manager/utilitySubscribers.jsp").forward(request, response);
                break;

        }
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
        String action = request.getParameter("action");
        utilitiesDAO dao = new utilitiesDAO();
        if (action == null) {
            action = "all";
        }
        switch (action) {
            case "add":
                String utilityName = request.getParameter("utilityName");
                String priceAdd = new String(request.getParameter("price"));
                String unit = request.getParameter("unit");

                // Validate rỗng
                if (utilityName == null || utilityName.trim().isEmpty()) {
                    request.getSession().setAttribute("errorMsg", "Utility name cannot be empty!");
                    response.sendRedirect(request.getContextPath() + "/manager/utilities");
                    break;
                }
                if (unit == null || unit.trim().isEmpty()) {
                    request.getSession().setAttribute("errorMsg", "Unit cannot be empty!");
                    response.sendRedirect(request.getContextPath() + "/manager/utilities");
                    break;
                }

                // Validate số thuần
                if (utilityName.trim().matches("\\d+")) {
                    request.getSession().setAttribute("errorMsg", "Utility name cannot be numbers only!");
                    response.sendRedirect(request.getContextPath() + "/manager/utilities");
                    break;
                }
                if (unit.trim().matches("\\d+")) {
                    request.getSession().setAttribute("errorMsg", "Unit cannot be numbers only!");
                    response.sendRedirect(request.getContextPath() + "/manager/utilities");
                    break;
                }

                // Validate price
                BigDecimal price = BigDecimal.ZERO;  // khởi tạo mặc định trước
                try {
                    price = new BigDecimal(priceAdd);
                    if (price.compareTo(BigDecimal.ZERO) < 0) {
                        request.getSession().setAttribute("errorMsg", "Price cannot be negative!");
                        response.sendRedirect(request.getContextPath() + "/manager/utilities");
                        break;
                    }
                } catch (NumberFormatException e) {
                    request.getSession().setAttribute("errorMsg", "Invalid price!");
                    response.sendRedirect(request.getContextPath() + "/manager/utilities");
                    break;
                }

                // Validate trùng tên
                if (dao.isUtilityNameExists(utilityName.trim())) {
                    request.getSession().setAttribute("errorMsg", "Utility name already exists!");
                    response.sendRedirect(request.getContextPath() + "/manager/utilities");
                    break;
                }

                //check giá k lớn hơn 10tr
                if (price.compareTo(new BigDecimal("10000000")) > 0) {
                    request.getSession().setAttribute("errorMsg", "Price cannot exceed 10,000,000 VND!");
                    response.sendRedirect(request.getContextPath() + "/manager/utilities");
                    break;
                }

                // Pass hết thì add
                boolean result = dao.addUtility(utilityName.trim(), price, unit.trim());
                if (result) {
                    request.getSession().setAttribute("successMsg", "Utility added successfully!");
                } else {
                    request.getSession().setAttribute("errorMsg", "Failed to add utility. Please try again!");
                }
                response.sendRedirect(request.getContextPath() + "/manager/utilities");
                break;

            case "delete":
                int id = Integer.parseInt(request.getParameter("id"));

                if (dao.isUtilityUsedInBill(id)) {
                    request.getSession().setAttribute("errorMsg",
                            "Cannot be deleted! This utility is being used in the invoice.");
                } else {
                    Boolean resDelete = dao.deleteUtilities(id);
                    if (resDelete) {
                        request.getSession().setAttribute("successMsg", "Extension successfully removed!");
                    } else {
                        request.getSession().setAttribute("errorMsg", "Clear failure, please try again!");
                    }
                }
                response.sendRedirect(request.getContextPath() + "/manager/utilities");
                break;

            case "edit":
                int idU = Integer.parseInt(request.getParameter("id"));
                BigDecimal priceU = new BigDecimal(request.getParameter("price"));
                Boolean resUpdate = dao.updateUtilities(idU, priceU);
                response.sendRedirect(request.getContextPath() + "/manager/utilities");
                break;
        }
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
