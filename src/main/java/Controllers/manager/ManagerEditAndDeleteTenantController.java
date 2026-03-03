package Controllers.manager;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
import Models.entity.Tenant;
import Services.tenant.TenantService;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author ADMIN
 */
@WebServlet(urlPatterns = {"/manager/tenant/edit"})
public class ManagerEditAndDeleteTenantController extends HttpServlet {

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
            out.println("<title>Servlet ManagerEditTenantController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ManagerEditTenantController at " + request.getContextPath() + "</h1>");
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

        int id = Integer.parseInt(request.getParameter("tenantId"));
        TenantService service = new TenantService();
        Tenant t = service.findById(id);

        if (t != null) {
            t.setFullName(request.getParameter("fullName"));
            t.setIdentityCode(request.getParameter("identityCode"));
            t.setPhoneNumber(request.getParameter("phoneNumber"));
            t.setEmail(request.getParameter("email"));
            t.setAddress(request.getParameter("address"));

            String dob = request.getParameter("dateOfBirth");
            if (dob != null && !dob.isEmpty()) {
                t.setDateOfBirth(java.sql.Date.valueOf(dob));
            }

            String gender = request.getParameter("gender");
            if (gender != null && !gender.isEmpty()) {
                t.setGender(Integer.parseInt(gender));
            }

            t.setAvatar(request.getParameter("avatar"));

            try {
                service.updateTenant(t);
            } catch (IllegalArgumentException ex) {
                // Validation thất bại → redirect về kèm ?error=...
                String page = request.getParameter("page");
                String keyword = request.getParameter("keyword");
                StringBuilder redirectUrl = new StringBuilder(request.getContextPath() + "/manager/tenants?error="
                        + java.net.URLEncoder.encode(ex.getMessage(), "UTF-8"));
                if (keyword != null && !keyword.isBlank()) {
                    redirectUrl.append("&keyword=").append(java.net.URLEncoder.encode(keyword, "UTF-8"));
                }
                if (page != null && !page.isBlank()) {
                    redirectUrl.append("&page=").append(page);
                }
                response.sendRedirect(redirectUrl.toString());
                return;
            }
        }

        // Redirect về đúng trang sau khi edit thành công
        String page = request.getParameter("page");
        String keyword = request.getParameter("keyword");
        StringBuilder redirectUrl = new StringBuilder(request.getContextPath() + "/manager/tenants");
        boolean hasQuery = false;
        if (keyword != null && !keyword.isBlank()) {
            redirectUrl.append("?keyword=").append(java.net.URLEncoder.encode(keyword, "UTF-8"));
            hasQuery = true;
        }
        if (page != null && !page.isBlank()) {
            redirectUrl.append(hasQuery ? "&" : "?").append("page=").append(page);
        }
        response.sendRedirect(redirectUrl.toString());
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