package Controllers.tenant;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.util.Set;

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

/**
 *
 * @author Duong Thien Nhan - CE190741
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 10 * 1024 * 1024, // 10MB
        maxRequestSize = 25 * 1024 * 1024 // 25MB
)
public class TenantAddOccupantController extends HttpServlet {

    private final ContractDAO contractDAO = new ContractDAO();
    private final OccupantDAO occupantDAO = new OccupantDAO();
    private final OccupantDocumentDAO occupantDocumentDAO = new OccupantDocumentDAO();

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("=== TenantAddOccupantController.doGet ENTERED ===");
        response.getWriter().println("TenantAddOccupantController GET OK");
    }

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        AuthResult auth = (session == null) ? null : (AuthResult) session.getAttribute("auth");

        if (auth == null || auth.getTenant() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String contractIdRaw = trimToNull(request.getParameter("contractId"));

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

        Contract contract = null;
        try {
            contract = contractDAO.findDetailForTenant(contractId, primaryTenantId);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        Path runtimeDir = null;
        Path sourceDir = null;
        String frontFile = null;
        String backFile = null;

        try (Connection conn = new DBContext().getConnection()) {
            conn.setAutoCommit(false);

            int currentOccupants = occupantDAO.countPendingOrActiveByContractId(conn, contractId);
            Integer maxTenantsObj = contract.getMaxTenants();

            if (maxTenantsObj == null || maxTenantsObj <= 0) {
                conn.rollback();
                response.sendRedirect(request.getContextPath()
                        + "/tenant/contract/detail?id=" + contractId + "&err=max");
                return;
            }

            // total room occupants = primary tenant + current occupants + new occupant
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

            if (occupantId <= 0) {
                conn.rollback();
                response.sendRedirect(request.getContextPath()
                        + "/tenant/contract/detail?id=" + contractId + "&err=occupant");
                return;
            }

            Part front = request.getPart("cccdFront");
            Part back = request.getPart("cccdBack");

            if (front == null || front.getSize() <= 0 || back == null || back.getSize() <= 0) {
                conn.rollback();
                response.sendRedirect(request.getContextPath()
                        + "/tenant/contract/detail?id=" + contractId + "&err=document");
                return;
            }

            String frontExt = getFileExtension(front);
            String backExt = getFileExtension(back);

            validateImageExtension(frontExt, "CCCD mặt trước");
            validateImageExtension(backExt, "CCCD mặt sau");

            String runtimeUploadPath = getServletContext().getRealPath("/assets/images/occupant-docs/");
            if (runtimeUploadPath == null || runtimeUploadPath.isBlank()) {
                throw new IllegalStateException("Không lấy được runtime upload path từ getRealPath().");
            }
            runtimeDir = Paths.get(runtimeUploadPath);

            String sourceUploadPath = getServletContext().getInitParameter("occupantDocsUploadDir");
            if (sourceUploadPath == null || sourceUploadPath.isBlank()) {
                throw new IllegalStateException("Thiếu context-param occupantDocsUploadDir trong web.xml.");
            }
            sourceDir = Paths.get(sourceUploadPath);

            Files.createDirectories(runtimeDir);
            Files.createDirectories(sourceDir);

            long now = System.currentTimeMillis();
            frontFile = now + "_occupant_" + occupantId + "_front" + frontExt;
            backFile = now + "_occupant_" + occupantId + "_back" + backExt;

            savePartToBothLocations(front, frontFile, runtimeDir, sourceDir);
            savePartToBothLocations(back, backFile, runtimeDir, sourceDir);

            String frontUrl = "/assets/images/occupant-docs/" + frontFile;
            String backUrl = "/assets/images/occupant-docs/" + backFile;

            int frontDocId = occupantDocumentDAO.insertDocument(conn, occupantId, "CCCD_FRONT", frontUrl);
            int backDocId = occupantDocumentDAO.insertDocument(conn, occupantId, "CCCD_BACK", backUrl);

            if (frontDocId <= 0 || backDocId <= 0) {
                conn.rollback();
                rollbackUploadedFiles(runtimeDir, sourceDir, frontFile, backFile);

                response.sendRedirect(request.getContextPath()
                        + "/tenant/contract/detail?id=" + contractId + "&err=document");
                return;
            }

            conn.commit();

            response.sendRedirect(request.getContextPath()
                    + "/tenant/contract/detail?id=" + contractId + "&added=1");

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            rollbackUploadedFiles(runtimeDir, sourceDir, frontFile, backFile);

            String msg = URLEncoder.encode(
                    e.getMessage() == null ? "Invalid input" : e.getMessage(),
                    StandardCharsets.UTF_8
            );
            response.sendRedirect(request.getContextPath()
                    + "/tenant/contract/detail?id=" + contractId + "&err=1&msg=" + msg);

        } catch (Exception e) {
            e.printStackTrace();
            rollbackUploadedFiles(runtimeDir, sourceDir, frontFile, backFile);

            String msg = URLEncoder.encode(
                    e.getMessage() == null ? "unknown" : e.getMessage(),
                    StandardCharsets.UTF_8
            );
            response.sendRedirect(request.getContextPath()
                    + "/tenant/contract/detail?id=" + contractId + "&err=1&msg=" + msg);
        }
    }

    private void savePartToBothLocations(Part part, String fileName, Path runtimeDir, Path sourceDir) throws IOException {
        Path runtimeFile = runtimeDir.resolve(fileName);
        Path sourceFile = sourceDir.resolve(fileName);

        try (InputStream in = part.getInputStream()) {
            Files.copy(in, runtimeFile, StandardCopyOption.REPLACE_EXISTING);
        }

        try (InputStream in = part.getInputStream()) {
            Files.copy(in, sourceFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void rollbackUploadedFiles(Path runtimeDir, Path sourceDir, String frontFile, String backFile) {
        deleteIfExists(runtimeDir, frontFile);
        deleteIfExists(runtimeDir, backFile);
        deleteIfExists(sourceDir, frontFile);
        deleteIfExists(sourceDir, backFile);
    }

    private void deleteIfExists(Path dir, String fileName) {
        if (dir == null || fileName == null || fileName.isBlank()) {
            return;
        }
        try {
            Files.deleteIfExists(dir.resolve(fileName));
        } catch (IOException ignored) {
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

    private void validateImageExtension(String ext, String fieldName) {
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException(fieldName + " chỉ chấp nhận file ảnh: .jpg, .jpeg, .png, .webp");
        }
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

        return submitted.substring(dot).toLowerCase();
    }
}
