/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.tenant;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 20 * 1024 * 1024)
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

    try {
        String category = request.getParameter("category");
        String description = request.getParameter("description");
        int roomId = Integer.parseInt(request.getParameter("roomId"));

        Integer utilityId = null;
        if ("ELECTRIC".equals(category) || "WATER".equals(category)) {
            String utilityName = "ELECTRIC".equals(category) ? "Electric" : "Water";
            utilityId = dao.getUtilityIdByName(utilityName);
        }

        List<String> images = new ArrayList<>();

        String runtimePath = getServletContext().getRealPath("/assets/images/maintenance");
        String sourcePath = getServletContext().getInitParameter("maintenanceImageDir");

        System.out.println("runtimePath = " + runtimePath);
        System.out.println("sourcePath = " + sourcePath);

        if (runtimePath == null || runtimePath.trim().isEmpty()) {
            throw new ServletException("runtimePath is null");
        }

        if (sourcePath == null || sourcePath.trim().isEmpty()) {
            throw new ServletException("maintenanceImageDir is missing");
        }

        File runtimeDir = new File(runtimePath);
        if (!runtimeDir.exists()) {
            runtimeDir.mkdirs();
        }

        File sourceDir = new File(sourcePath);
        if (!sourceDir.exists()) {
            sourceDir.mkdirs();
        }

        for (Part part : request.getParts()) {
            if (!"images".equals(part.getName())) {
                continue;
            }

            if (part.getSize() == 0) {
                continue;
            }

            if (images.size() >= 3) {
                List<TenantMyRoomDTO> rooms = dao.getRoomsByTenantId(tenantId);
                request.setAttribute("error", "You can upload a maximum of 3 images!");
                request.setAttribute("rooms", rooms);
                request.getRequestDispatcher("/views/tenant/createMaintenance.jsp").forward(request, response);
                return;
            }

            String contentType = part.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                List<TenantMyRoomDTO> rooms = dao.getRoomsByTenantId(tenantId);
                request.setAttribute("error", "Only image files are allowed!");
                request.setAttribute("rooms", rooms);
                request.getRequestDispatcher("/views/tenant/createMaintenance.jsp").forward(request, response);
                return;
            }

            String originalFileName = part.getSubmittedFileName();
            String ext = "";
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex >= 0) {
                ext = originalFileName.substring(dotIndex);
            }

            String fileName = java.util.UUID.randomUUID().toString() + ext;

            File runtimeFile = new File(runtimeDir, fileName);
            File sourceFile = new File(sourceDir, fileName);

            part.write(runtimeFile.getAbsolutePath());
            Files.copy(runtimeFile.toPath(), sourceFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            images.add(fileName);
        }

        String imageString = String.join(",", images);
        System.out.println("imageString = " + imageString);

        dao.createRequest(tenantId, roomId, category, description, imageString, utilityId);

        response.sendRedirect(request.getContextPath() + "/tenant/maintenance");

    } catch (Exception e) {
        e.printStackTrace();

        List<TenantMyRoomDTO> rooms = dao.getRoomsByTenantId(tenantId);
        request.setAttribute("error", "Có lỗi khi gửi yêu cầu bảo trì: " + e.getMessage());
        request.setAttribute("rooms", rooms);
        request.getRequestDispatcher("/views/tenant/createMaintenance.jsp").forward(request, response);
    }
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
