package Controllers.tenant;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

import DALs.contract.ContractDAO;
import DALs.contract.OccupantDAO;
import DALs.contract.OccupantDocumentDAO;
import Models.authentication.AuthResult;
import Models.entity.Contract;
import Models.entity.Occupant;
import Utils.database.DBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 10 * 1024 * 1024, // 10MB
        maxRequestSize = 25 * 1024 * 1024 // 25MB
)
public class TenantAddOccupantController extends HttpServlet {

    private final ContractDAO contractDAO = new ContractDAO();
    private final OccupantDAO occupantDAO = new OccupantDAO();
    private final OccupantDocumentDAO occupantDocumentDAO = new OccupantDocumentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("=== TenantAddOccupantController.doGet ENTERED ===");
        response.getWriter().println("TenantAddOccupantController GET OK");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.err.println("=== TenantAddOccupantController.doPost ENTERED ===");
        System.err.println("requestURI = " + request.getRequestURI());
        System.err.println("contentType = " + request.getContentType());

        HttpSession session = request.getSession(false);
        AuthResult auth = (session == null) ? null : (AuthResult) session.getAttribute("auth");

        if (auth == null || auth.getTenant() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Ưu tiên lấy từ query string, fallback sang form field
        String contractIdRaw = trimToNull(request.getParameter("contractId"));
        System.err.println("contractIdRaw = [" + contractIdRaw + "]");

        if (contractIdRaw == null) {
            response.sendRedirect(request.getContextPath() + "/tenant/contract?err=missingContractId");
            return;
        }

        int contractId;
        try {
            contractId = Integer.parseInt(contractIdRaw);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/tenant/contract?err=badContractId");
            return;
        }

        int primaryTenantId = auth.getTenant().getTenantId();

        System.err.println("primaryTenantId = " + primaryTenantId);
        System.err.println("contractId = " + contractId);

        Contract contract = null;
        try {
            contract = contractDAO.findDetailForTenant(contractId, primaryTenantId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.err.println("contract found = " + (contract != null));

        if (contract == null) {
            response.sendRedirect(request.getContextPath()
                    + "/tenant/contract/detail?id=" + contractId + "&err=contractNotFound");
            return;
        }

        if (!"PENDING".equalsIgnoreCase(contract.getStatus())) {
            response.sendRedirect(request.getContextPath()
                    + "/tenant/contract/detail?id=" + contractId + "&err=notpending");
            return;
        }

        try (Connection conn = new DBContext().getConnection()) {
            conn.setAutoCommit(false);

            int currentOccupants = occupantDAO.countPendingOrActiveByContractId(conn, contractId);
            Integer maxTenantsObj = contract.getMaxTenants();

            System.err.println("currentOccupants = " + currentOccupants);
            System.err.println("maxTenants = " + maxTenantsObj);

            if (maxTenantsObj == null || maxTenantsObj <= 0) {
                conn.rollback();
                response.sendRedirect(request.getContextPath()
                        + "/tenant/contract/detail?id=" + contractId + "&err=max");
                return;
            }

            /*
             * Nếu maxTenants là tổng số người tối đa trong phòng, bao gồm tenant chính:
             * totalAfterAdd = 1 tenant chính + currentOccupants + 1 occupant mới
             */
            int totalAfterAdd = 1 + currentOccupants + 1;
            if (totalAfterAdd > maxTenantsObj) {
                conn.rollback();
                response.sendRedirect(request.getContextPath()
                        + "/tenant/contract/detail?id=" + contractId + "&err=max");
                return;
            }

            Occupant o = new Occupant();
            o.setContractId(contractId);
            o.setFullName(required(request, "fullName"));
            o.setIdentityCode(required(request, "identityCode"));
            o.setPhoneNumber(required(request, "phone"));
            o.setEmail(required(request, "email"));
            o.setAddress(required(request, "address"));
            o.setGender(parseGender(required(request, "gender")));
            o.setDateOfBirth(java.sql.Date.valueOf(required(request, "dob")));
            o.setStatus("PENDING");

            int occupantId = occupantDAO.insertOccupant(conn, o);
            System.err.println("occupantId = " + occupantId);

            if (occupantId <= 0) {
                conn.rollback();
                response.sendRedirect(request.getContextPath()
                        + "/tenant/contract/detail?id=" + contractId + "&err=occupant");
                return;
            }

            String uploadPath = resolveOccupantUploadPath();
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists() && !uploadDir.mkdirs()) {
                conn.rollback();
                response.sendRedirect(request.getContextPath()
                        + "/tenant/contract/detail?id=" + contractId + "&err=document");
                return;
            }

            Part front = request.getPart("cccdFront");
            Part back = request.getPart("cccdBack");

            System.err.println("front part null = " + (front == null));
            System.err.println("back part null = " + (back == null));
            System.err.println("front size = " + (front == null ? -1 : front.getSize()));
            System.err.println("back size = " + (back == null ? -1 : back.getSize()));
            System.err.println("uploadPath = " + uploadPath);

            if (front == null || front.getSize() <= 0 || back == null || back.getSize() <= 0) {
                conn.rollback();
                response.sendRedirect(request.getContextPath()
                        + "/tenant/contract/detail?id=" + contractId + "&err=document");
                return;
            }

            String frontExt = getFileExtension(front);
            String backExt = getFileExtension(back);

            long now = System.currentTimeMillis();
            String frontFile = now + "_occupant_" + occupantId + "_front" + frontExt;
            String backFile = now + "_occupant_" + occupantId + "_back" + backExt;

            front.write(new File(uploadDir, frontFile).getAbsolutePath());
            back.write(new File(uploadDir, backFile).getAbsolutePath());

            String frontUrl = "/assets/images/occupant-docs/" + frontFile;
            String backUrl = "/assets/images/occupant-docs/" + backFile;

            int frontDocId = occupantDocumentDAO.insertDocument(conn, occupantId, "CCCD_FRONT", frontUrl);
            int backDocId = occupantDocumentDAO.insertDocument(conn, occupantId, "CCCD_BACK", backUrl);

            System.err.println("frontDocId = " + frontDocId);
            System.err.println("backDocId = " + backDocId);

            if (frontDocId <= 0 || backDocId <= 0) {
                conn.rollback();
                response.sendRedirect(request.getContextPath()
                        + "/tenant/contract/detail?id=" + contractId + "&err=document");
                return;
            }

            conn.commit();

            response.sendRedirect(request.getContextPath()
                    + "/tenant/contract/detail?id=" + contractId + "&added=1");

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            String msg = URLEncoder.encode(
                    e.getMessage() == null ? "Invalid input" : e.getMessage(),
                    StandardCharsets.UTF_8
            );
            response.sendRedirect(request.getContextPath()
                    + "/tenant/contract/detail?id=" + contractId + "&err=1&msg=" + msg);

        } catch (Exception e) {
            e.printStackTrace();
            String msg = URLEncoder.encode(
                    e.getMessage() == null ? "unknown" : e.getMessage(),
                    StandardCharsets.UTF_8
            );
            response.sendRedirect(request.getContextPath()
                    + "/tenant/contract/detail?id=" + contractId + "&err=1&msg=" + msg);
        }
    }

    private String required(HttpServletRequest request, String name) {
        String v = request.getParameter(name);
        if (v == null || v.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing field: " + name);
        }
        return v.trim();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        return value.isEmpty() ? null : value;
    }

    private Integer parseGender(String raw) {
        if ("1".equals(raw)) {
            return 1;
        }
        if ("0".equals(raw)) {
            return 0;
        }
        throw new IllegalArgumentException("Invalid gender");
    }

    private String getFileExtension(Part part) {
        String submitted = part.getSubmittedFileName();
        if (submitted == null || submitted.isBlank()) {
            return ".jpg";
        }
        int dot = submitted.lastIndexOf('.');
        if (dot < 0) {
            return ".jpg";
        }
        String ext = submitted.substring(dot).toLowerCase();
        switch (ext) {
            case ".jpg":
            case ".jpeg":
            case ".png":
            case ".webp":
                return ext;
            default:
                return ".jpg";
        }
    }

    private String resolveOccupantUploadPath() {
        String realPath = getServletContext().getRealPath("/assets/images/occupant-docs/");
        if (realPath != null && !realPath.isBlank()) {
            return realPath;
        }

        return System.getProperty("user.dir")
                + File.separator + "src"
                + File.separator + "main"
                + File.separator + "webapp"
                + File.separator + "assets"
                + File.separator + "images"
                + File.separator + "occupant-docs";
    }
}
