/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.tenant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import DALs.maintenanceRequest.MaintenanceRequestDAO;
import Models.authentication.AuthResult;
import Models.dto.MaintenanceRequestDTO;
import Models.dto.TenantMyRoomDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

/**
 *
 * @author truon
 */
@WebServlet("/tenant/maintenance")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 20 * 1024 * 1024
)
public class MaintenanceRequestForTenantController extends HttpServlet {

    private final MaintenanceRequestDAO dao = new MaintenanceRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        AuthResult user = (AuthResult) request.getSession().getAttribute("auth");
        if (user == null || user.getTenant() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String action = request.getParameter("action");
        if ("view".equals(action)) {
            showMaintenanceDetail(request, response, user);
            return;
        }
        if ("create".equals(action)) {
            int tenantId = user.getTenant().getTenantId();
            List<TenantMyRoomDTO> rooms = dao.getRoomsByTenantId(tenantId);
            request.setAttribute("rooms", rooms);
            request.getRequestDispatcher("/views/tenant/createMaintenance.jsp")
                    .forward(request, response);
            return;
        }
        int tenantId = user.getTenant().getTenantId();
        int pageSize = 10;
        int pageIndex = 1;
        String page = request.getParameter("page");
        if (page != null) {
            try {
                pageIndex = Integer.parseInt(page);
            } catch (NumberFormatException e) {
                pageIndex = 1;
            }
        }

        int totalRequest = dao.countRequestByTenantId(tenantId);
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

        List<MaintenanceRequestDTO> list = dao.getRequestsByTenantId(tenantId, pageIndex, pageSize);
        request.setAttribute("requests", list);
        request.setAttribute("totalRequest", totalRequest);
        request.setAttribute("pageIndex", pageIndex);
        request.setAttribute("totalPage", totalPage);
        request.getRequestDispatcher("/views/tenant/maintenanceListForTenant.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        AuthResult user = (AuthResult) request.getSession().getAttribute("auth");
        if (user == null || user.getTenant() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int tenantId = user.getTenant().getTenantId();
        String category = request.getParameter("category");
        String description = request.getParameter("description");
        int roomId = Integer.parseInt(request.getParameter("roomId"));

        Integer utilityId = null;
        if ("ELECTRIC".equals(category) || "WATER".equals(category)) {
            String utilityName = "ELECTRIC".equals(category) ? "Electric" : "Water";
            utilityId = dao.getUtilityIdByName(utilityName);
        }

        List<String> images = new ArrayList<>();

        for (Part part : request.getParts()) {

            if (!"images".equals(part.getName())) {
                continue;
            }

            if (part.getSize() == 0) {
                continue;
            }

            if (images.size() >= 3) {
                tenantId = user.getTenant().getTenantId();
                List<TenantMyRoomDTO> rooms = dao.getRoomsByTenantId(tenantId);
                request.setAttribute("error", "You can upload a maximum of 3 images!");
                request.setAttribute("rooms", rooms);
                request.getRequestDispatcher("/views/tenant/createMaintenance.jsp")
                        .forward(request, response);
                return;
            }

            String fileName = System.currentTimeMillis() + "_" + part.getSubmittedFileName();
            //vao web.xml sua link lai cho dung nhe
            String uploadPath = getServletContext().getInitParameter("maintenanceImageDir");

            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            part.write(uploadPath + File.separator + fileName);
            images.add(fileName);
        }

        String imageString = String.join(",", images);
        dao.createRequest(tenantId, roomId, category, description, imageString, utilityId);
        response.sendRedirect(request.getContextPath() + "/tenant/maintenance");
    }

    private void showMaintenanceDetail(HttpServletRequest request,
            HttpServletResponse response,
            AuthResult user)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            MaintenanceRequestDTO maintenance = dao.getRequestById(id);
            if (maintenance == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            if (maintenance.getTenantId() != user.getTenant().getTenantId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            request.setAttribute("maintenance", maintenance);
            request.getRequestDispatcher("/views/tenant/viewMaintenance.jsp")
                    .forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
