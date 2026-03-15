package Controllers.tenant;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import DALs.auth.TenantDAO;
import DALs.auth.TenantDocumentDAO;
import DALs.contract.ContractDAO;
import DALs.contract.ContractOccupantDAO;
import Models.authentication.AuthResult;
import Models.entity.Contract;
import Models.entity.Tenant;
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
@MultipartConfig
public class TenantAddOccupantController extends HttpServlet {

    private final TenantDAO tenantDAO = new TenantDAO();
    private final ContractDAO contractDAO = new ContractDAO();
    private final ContractOccupantDAO occupantDAO = new ContractOccupantDAO();
    private final TenantDocumentDAO docDAO = new TenantDocumentDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        AuthResult auth = (session == null) ? null : (AuthResult) session.getAttribute("auth");

        if (auth == null || auth.getTenant() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int primaryTenantId = auth.getTenant().getTenantId();
        int contractId = Integer.parseInt(request.getParameter("contractId"));

        Contract contract = contractDAO.findDetailForTenant(contractId, primaryTenantId);
        if (contract == null) {
            response.sendRedirect(request.getContextPath() + "/tenant/contract");
            return;
        }

        try (Connection conn = new DBContext().getConnection()) {

            conn.setAutoCommit(false);

            Tenant t = new Tenant();
            t.setFullName(request.getParameter("fullName"));
            t.setIdentityCode(request.getParameter("identityCode"));
            t.setPhoneNumber(request.getParameter("phone"));
            t.setEmail(request.getParameter("email"));
            t.setAddress(request.getParameter("address"));
            t.setGender(Integer.parseInt(request.getParameter("gender")));
            t.setDateOfBirth(java.sql.Date.valueOf(request.getParameter("dob")));
            t.setAvatar("/assets/images/default-avatar.png");

            int tenantId = tenantDAO.insertPendingTenant(conn, t);
            if (tenantId <= 0) {
                conn.rollback();
                response.sendRedirect(request.getContextPath() + "/tenant/contract/detail?id=" + contractId + "&err=tenant");
                return;
            }

            int occId = occupantDAO.insertMember(
                    conn,
                    contractId,
                    tenantId,
                    contract.getStartDate(),
                    "PENDING"
            );

            if (occId <= 0) {
                conn.rollback();
                response.sendRedirect(request.getContextPath() + "/tenant/contract/detail?id=" + contractId + "&err=occupant");
                return;
            }

            String uploadPath = getServletContext().getRealPath("/assets/images/tenant-docs/");
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            Part front = request.getPart("cccdFront");
            Part back = request.getPart("cccdBack");

            String frontFile = System.currentTimeMillis() + "_front.jpg";
            String backFile = System.currentTimeMillis() + "_back.jpg";

            front.write(uploadPath + File.separator + frontFile);
            back.write(uploadPath + File.separator + backFile);

            docDAO.insertDocument(conn, tenantId, "CCCD_FRONT", "/assets/images/tenant-docs/" + frontFile);
            docDAO.insertDocument(conn, tenantId, "CCCD_BACK", "/assets/images/tenant-docs/" + backFile);

            conn.commit();

            response.sendRedirect(request.getContextPath() + "/tenant/contract/detail?id=" + contractId + "&added=1");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/tenant/contract/detail?id=" + contractId + "&err=1");
        }
    }
}
