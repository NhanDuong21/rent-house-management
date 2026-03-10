/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.manager;

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
        if (action == null) {
            action = "all";
        }
        switch (action) {
            case "all":
                List<Utility> listU = dao.getManagerUntilities();
                request.setAttribute("utilities", listU);
                request.getRequestDispatcher("/views/manager/utilities.jsp").forward(request, response);
                break;
                
            case "add":
                List<Utility> listAdd = dao.getManagerUntilities();
                request.setAttribute("utilities", listAdd);
                request.getRequestDispatcher("/views/manager/utilities.jsp").forward(request, response);
                break;
                
            case "delete":
                int idDelete = Integer.parseInt(request.getParameter("id"));
                dao.deleteUtilities(idDelete);
                response.sendRedirect(request.getContextPath() + "/manager/utilities");
                break;
                
            case "edit":
                int idEdit = Integer.parseInt(request.getParameter("id"));
                Utility uEdit = dao.getUtilityById(idEdit);
                List<Utility> listEdit = dao.getManagerUntilities();
                request.setAttribute("utilities", listEdit);
                request.setAttribute("editUtility", uEdit);
                request.getRequestDispatcher("/views/manager/utilities.jsp").forward(request, response);
                break; 
                
            case "subscribers":
                int idSub = Integer.parseInt(request.getParameter("id"));
                String nameSub = request.getParameter("name");
                List<Utility> subscribers = dao.getSubscribersByUtilityId(idSub);
                List<Utility> listSub = dao.getManagerUntilities();
                request.setAttribute("utilities", listSub);
                request.setAttribute("subscribers", subscribers);
                request.setAttribute("utilityName", nameSub);
                request.getRequestDispatcher("/views/manager/utilities.jsp").forward(request, response);
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
                BigDecimal price = new BigDecimal(request.getParameter("price"));
                String unit = request.getParameter("unit");

                Boolean result = dao.addUtility(utilityName, price, unit);
                response.sendRedirect(request.getContextPath() + "/manager/utilities");
                break;
            
            case "delete": 
                int id = Integer.parseInt(request.getParameter("id"));
                Boolean resDelete = dao.deleteUtilities(id);
                response.sendRedirect(request.getContextPath() + "/manager/utilities");
                break;
            
            case "edit": 
               int idU = Integer.parseInt(request.getParameter("id"));
               BigDecimal priceU = new BigDecimal(request.getParameter("price"));
               Boolean resUpdate = dao.updateUtilities(idU, priceU);
               response.sendRedirect(request.getContextPath()+"/manager/utilities");
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
